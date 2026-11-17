package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //1.保存菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.save(dish);
        //2.保存菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();//获取口味数据
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));//设置菜品id
            dishFlavorMapper.insertBatch(flavors);//批量保存菜品口味
        }
    }

    /**
     * 菜品分页查询
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1.分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 根据ID删除菜品
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否关联了分类
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishId(ids);
        if(setMealIds != null && setMealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品以及口味
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }*/
        //根据id批量删除菜品
        dishMapper.deleteByIds(ids);
        //根据id批量删除菜品口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据ID查询菜品和口味
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //1.根据id查询菜品
        Dish dish = dishMapper.getById(id);
        //2.根据菜品id查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //3.将菜品和口味数据封装到DishVO中并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //1.更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //2.删除原来的口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //3.更新为新的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
