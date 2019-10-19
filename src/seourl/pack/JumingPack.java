/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.pack;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seourl.pack.ex.PackAbstract;

/**
 *
 * @author Yuri
 */
@ToString
public class JumingPack extends PackAbstract {

    @Getter
    @Setter
    boolean reg = false;
    @Getter
    @Setter
    boolean qq = false;
    @Getter
    @Setter
    boolean weChat = false;
    @Getter
    @Setter
    boolean gfw = false;

    @Getter
    @Setter
    boolean error = false;

    public boolean isPass() {
        return !reg && !qq && !weChat && !gfw && !error;
    }

    public String getStatus() {
        String tmp = "";
        if (this.isReg()) {
            tmp += "己被註冊  ";
        }
        if (this.isQq()) {
            tmp += "QQ欄截  ";
        }
        if (this.isWeChat()) {
            tmp += "微信欄截  ";
        }
        if (this.isGfw()) {
            tmp += "被墙污染  ";
        }
        if (this.isError()) {
            tmp = "無法查詢  ";
        }
        if (this.isPass()) {
            tmp = "通過";
        }
        return tmp;
    }
}
