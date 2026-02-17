package com.grape.grape.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.grape.grape.config.Kaptcha.AdvancedMathTextCreator;
import com.grape.grape.config.Kaptcha.MathExpressionEvaluator;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:Gin.44.Candy
 * @Date: 2025/10/14  15:43
 * @Version
 */

@RestController
@RequestMapping("/captcha")
public class CaptchaController {


    @Resource
    private DefaultKaptcha advancedMathKaptcha;

    //线程安全的全局HashMap
    private static HashMap<String, Double> captchaMap = new java.util.HashMap<>();

    // 生成验证码图片
    @GetMapping("/gen")
    public void captcha(@RequestParam String captchaId, HttpServletResponse response, HttpSession session) throws IOException {
        // 如 "√(3+5)"
        String expression = advancedMathKaptcha.createText();
        System.out.println("验证码计算："+expression);
        // 验证表达式不为空
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalStateException("Failed to generate captcha expression");
        }

        BufferedImage image = advancedMathKaptcha.createImage(expression);

        // 存储表达式对应的答案（需提前计算）
        // 如 √(3+5)=2.828
        // 根号太复杂，做不了
        double answer = MathExpressionEvaluator.evaluateExpression(expression);
        System.out.println(answer);
//        session.setAttribute("captchaAnswer",  answer);
        captchaMap.put(captchaId,  answer);
        // 输出图片流
        response.setContentType("image/jpeg");
        ImageIO.write(image,  "jpg", response.getOutputStream());
    }

    // 验证用户输入
    @PostMapping("/verify")
    public Resp verify(@RequestParam String captchaId,@RequestParam double userInput, HttpSession session) {
//        Double answer = (Double) session.getAttribute("captchaAnswer");
        Double answer = captchaMap.get(captchaId);
        // 允许浮点数误差
        if (answer != null && Math.abs(userInput  - answer) < 0.01) {
            return Resp.ok(" 验证成功");
        }
        return Resp.info(ResultEnumI18n.CODE_ERROR.getCode(), "验证码错误");
    }

    // 计算表达式值（需实现）
public static double calculateSimpleExpr(String expression) {
    if (expression == null || expression.trim().isEmpty()) {
        throw new IllegalArgumentException("Expression cannot be null or empty");
    }

    try {
        return new MathExpressionEvaluator(expression.trim()).parseExpression();
    } catch (Exception e) {
        throw new IllegalArgumentException("Failed to evaluate expression: " + expression, e);
    }
}




}
