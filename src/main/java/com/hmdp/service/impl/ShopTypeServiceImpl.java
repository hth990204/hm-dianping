package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        // 1.查询缓存
        List<String> shopTypeListInRedis = stringRedisTemplate.opsForList().range(SHOP_TYPE_KEY, 0, -1);

        // 2.判断是否存在
        if (!shopTypeListInRedis.isEmpty()) {
            // 3.存在就返回
            List<ShopType> shopTypeList = shopTypeListInRedis.stream().map(item -> {
                return JSONUtil.toBean(item, ShopType.class);
            }).collect(Collectors.toList());

            return Result.ok(shopTypeList);
        }

        // 4.不存在，查数据库
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        List<String> resList = shopTypeList.stream().map(item -> {
            return JSONUtil.toJsonStr(item);
        }).collect(Collectors.toList());

        // 5.写入redis
        stringRedisTemplate.opsForList().rightPushAll(SHOP_TYPE_KEY, resList);

        // 6.返回
        return Result.ok(shopTypeList);
    }
}
