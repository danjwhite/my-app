package com.example.myapp.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
class UUIDBaseEntity extends BaseEntity {

    @Type(type="uuid-char")
    private UUID guid;

    UUIDBaseEntity() {
        guid = UUID.randomUUID();
    }
}
