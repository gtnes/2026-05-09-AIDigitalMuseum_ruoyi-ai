package org.ruoyi.common.core.utils.ip;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;

/**
 * 根据ip地址定位工具类，离线方式
 * 参考地址：<a href="https://gitee.com/lionsoul/ip2region/tree/master/binding/java">集成 ip2region 实现离线IP地址定位库</a>
 *
 * @author lishuyan
 */
@Slf4j
public class RegionUtils {

    // IP地址库文件名称
    public static final String IP_XDB_FILENAME = "ip2region.xdb";

    private static final Searcher SEARCHER;

    static {
        Searcher searcher;
        try {
            // 1、将 ip2region 数据库文件 xdb 从 ClassPath 加载到内存。
            // 2、基于加载到内存的 xdb 数据创建一个 Searcher 查询对象。
            searcher = Searcher.newWithBuffer(ResourceUtil.readBytes(IP_XDB_FILENAME));
            log.info("RegionUtils初始化成功，加载IP地址库数据成功！");
        } catch (Throwable e) {
            // 初始化失败降级（jar缺少xdb/堆内存不足等），归属地显示"未知"，不阻断登录/记账/合成等主流程；
            // 注意：静态块中抛出异常会导致该类在整个JVM生命周期内永久不可用（NoClassDefFoundError）
            searcher = null;
            log.error("RegionUtils初始化失败，IP归属地将显示为【未知】，请检查ip2region.xdb是否随jar部署及JVM堆内存是否充足", e);
        }
        SEARCHER = searcher;
    }

    /**
     * 根据IP地址离线获取城市
     */
    public static String getCityInfo(String ip) {
        if (SEARCHER == null) {
            return "未知";
        }
        try {
            // 3、执行查询
            String region = SEARCHER.search(StringUtils.trim(ip));
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip);
            return "未知";
        }
    }

}
