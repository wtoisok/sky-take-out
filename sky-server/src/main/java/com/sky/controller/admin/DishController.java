package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        // 清空缓存
        deleteCache("dish_*");
        return Result.success();
    }
    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 根据ID删除菜品
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("根据ID删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        // 清空缓存
        deleteCache("dish_*");
        return Result.success();
    }
    /**
     * 根据ID查询菜品
     */
    @GetMapping("/{id}")
    public Result<DishVO> get(@PathVariable Long id){
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        Long categoryId = dishDTO.getCategoryId();
        String key = "dish_" + categoryId;
        deleteCache(key);

        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);

        // 清空缓存
        deleteCache("dish_*");
        return Result.success();
    }
    /**
     * 根据分类ID查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam Long categoryId) {
        List<Dish> dishes = dishService.list(categoryId);
        return Result.success(dishes);
    }

    /**
     * 清空redis缓存
     * @param pattern
     */
    private void deleteCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
