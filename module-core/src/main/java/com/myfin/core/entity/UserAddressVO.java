package com.myfin.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class UserAddressVO {

    /** 유저의 우편번호 */
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    /** 유저의 도로명 주소 */
    @Column(name = "address_1", nullable = false)
    private String address1;

    /** 유저의 상세 주소 */
    @Column(name = "address_2")
    private String address2;

    static UserAddressVO of(String zipCode, String address1, String address2) {
        return new UserAddressVO(zipCode, address1, address2);
    }

}
