package com.lwl.miaosha.validator;

import com.lwl.miaosha.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import java.lang.annotation.Annotation;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
//自定义注解的校验类

/**
 * @param IsMobile 自定义注解类
 * @param String   需要校验的数据类型
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    //初始化方法，判断注解中的参数是否可以为空
    @Override
    public void initialize(IsMobile isMobile) {
        required = isMobile.required();
    }

    //校验方法
    // s 为待校验的值
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required == true || !StringUtils.isEmpty(s)){ //参数不能为空时或者s中有数据时,则判断是否合法
            return ValidatorUtil.isMobile(s);
        }else { //可以为空
            return true;
        }
    }
}
