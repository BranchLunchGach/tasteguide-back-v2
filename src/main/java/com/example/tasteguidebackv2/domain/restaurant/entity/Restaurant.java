package com.example.tasteguidebackv2.domain.restaurant.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.tasteguidebackv2.common.base.BaseEntity;
import com.example.tasteguidebackv2.domain.menu.entity.Menu;
import com.example.tasteguidebackv2.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 매장 이름 (기존 restaurantName)

    private String type; // 매장 타입 (기존 restaurantType)

    private boolean dayOff; // 휴무 유무

    private String address; // 매장 도로명 주소

    private String subway; // 지하철역 (기존 subwayAddress)

    private String horoscope; // 별점

    private String visitorReviewCnt;

    private String blogReviewCnt;

    @ElementCollection
    private List<String> keywordReviews = new ArrayList<>();

    @ElementCollection
    private List<String> textReviews = new ArrayList<>();

    private String info; // 매장 소개 (기존 restauranInfo)

    private String service; // 매장 편의시설 및 서비스 (기존 restauranService)

    private String mainImg;

    private int distance; // (기존 restauranDistance)

    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

}
