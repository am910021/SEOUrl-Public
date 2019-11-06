/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.other;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Yuri
 */
@ToString
@EqualsAndHashCode
public class TPair<L, M, R> {

    final private L left;
    final private M mid;
    final private R right;

    public TPair(L left, M mid, R right) {
        super();
        this.left = left;
        this.mid = mid;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public M getMid() {
        return mid;
    }

    public R getRight() {
        return right;
    }

}
