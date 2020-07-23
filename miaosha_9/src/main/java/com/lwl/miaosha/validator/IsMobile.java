package com.lwl.miaosha.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class} //指定一个校验器进行校验
)
public @interface IsMobile {

    boolean required() default true; //默认这个参数必须得有

    String message() default "{手机号码格式有误!}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
