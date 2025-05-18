package com.bb.webcanvasservice.common.ban;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ban")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ban {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    public Ban(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
