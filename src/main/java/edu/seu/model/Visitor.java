package edu.seu.model;

import org.springframework.stereotype.Component;

/**
 * 未登陆用户
 */
@Component
public class Visitor extends User {
    public Visitor()
    {
        setId(-1);
    }
}
