package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据id查询购物车
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 购物车数量加1
     * @param id
     */
    @Update("update shopping_cart set number = number + 1 where id = #{id}")
    void updateNumberById(Long id);

    /**
     * 修改购物车数量
     *
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumber(ShoppingCart shoppingCart);
    /**
     * 添加购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, amount, image, number,dish_flavor, create_time) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{amount}, #{image}, #{number},#{dishFlavor},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void cleanShoppingCart(Long userId);

    /**
     * 根据id删除购物车
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
