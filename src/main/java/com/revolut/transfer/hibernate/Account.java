package com.revolut.transfer.hibernate;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ACCOUNTS")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "BALANCE", precision = 10, scale = 2)
    @Min(value = 0)
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                balance.compareTo(account.balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }
}
