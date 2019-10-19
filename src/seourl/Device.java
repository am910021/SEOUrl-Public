/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl;

import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Yuri
 */
@ToString
public enum Device {
    WINDOWS_CHROME74(0, 0, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"),
    WINDOWS_CHROME72(1, 0, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"),
    WINDOWS_FIREFOX67(2, 0, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0"),
    WINDOWS_FIREFOX66(3, 0, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0"),
    MAC_SAFAIR121(4, 0, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.1 Safari/605.1.15"),
    MAC_SAFAIR111(5, 0, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15"),
    LINUX_CHROME74(6, 0, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/74.0.3729.157 Safari/537.36"),
    LINUX_FIREFOX49(7, 0, "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");

    @Getter
    private int id;

    @Getter
    private int rate;

    @Getter
    private String type;

    private Device(int id, int r, String u) {
        this.id = id;
        this.rate = r;
        this.type = u;
    }

    public static Device getById(int id) {
        for (Device d : Device.values()) {
            if (id == d.getId()) {
                return d;
            }
        }
        return Device.WINDOWS_CHROME74;
    }

}
