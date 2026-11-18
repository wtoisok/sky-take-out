package com.sky.mapper;


import com.sky.entity.ShoppingCart;
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
     * 修改购物车数量
     * @param id
     */
    @Update("update shopping_cart set number = number + 1 where id = #{id}")
    void updateNumberById(Long id);

    /**
     * 添加购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, amount, image, number,dish_flavor, create_time) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{amount}, #{image}, #{number},#{dishFlavor},#{createTime})")
    void insert(ShoppingCart shoppingCart);
}
