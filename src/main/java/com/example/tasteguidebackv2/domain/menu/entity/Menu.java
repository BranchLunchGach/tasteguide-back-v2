package com.example.tasteguidebackv2.domain.menu.entity;

import com.example.tasteguidebackv2.common.base.BaseEntity;
import com.example.tasteguidebackv2.domain.restaurant.entity.Restaurant;
import com.example.tasteguidebackv2.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category; // ex) 밥류, 면류

    @Column(nullable = false)
    private String name; // 메뉴 이름

    private int calorie;

    private double carbohydrate;

    private double fat;

    private double protein;

    private String nation; // ex) 한식, 중식

    private boolean soup;

    private String season;
    private String holiday;
    private String time;
    private String weather;

    private String keyword;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id") // FK: 메뉴가 속한 레스토랑
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
