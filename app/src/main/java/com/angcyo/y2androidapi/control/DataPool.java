package com.angcyo.y2androidapi.control;

import com.angcyo.y2androidapi.moudle.AndroidClassLinkBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class DataPool {
    public List<AndroidClassLinkBean> packLinks;//所有的包
    public List<AndroidClassLinkBean> classLinks;//所有包下的类

    private List<String> typeList;//所有类别
    private List<Integer> typeCountList;//类别对应的数据数量
    private List<Integer> typeIndex;//分类所在的位置position

    public List<AndroidClassLinkBean> allLinks;

    public DataPool parse() {
        typeList = new ArrayList<>();
        typeCountList = new ArrayList<>();
        allLinks = new ArrayList<>();
        typeIndex = new ArrayList<>();

        if (packLinks != null) {
            typeList.add(packLinks.get(0).getClassify());
            typeIndex.add(0);
            typeCountList.add(packLinks.size());
            allLinks.addAll(packLinks);
        }

        String typeName = "-";
        String lastAddTypeName = "-";
        int count = 0;
        if (classLinks != null) {
            for (AndroidClassLinkBean link : classLinks) {
                count++;
                if (!link.getClassify().equalsIgnoreCase(typeName)) {
                    typeList.add(link.getClassify());
                    if (!typeName.equalsIgnoreCase("-")) {
                        typeIndex.add(typeIndex.get((typeIndex.size() - 1) < 0 ? 0 : typeIndex.size() - 1) + 1 + typeCountList.get((typeCountList.size() - 1) < 0 ? 0 : typeCountList.size() - 1));//类别所在的位置
                        typeCountList.add(count);
                        count = 0;
                        lastAddTypeName = typeName;
                    }
                    typeName = link.getClassify();
                }
            }
            if (!lastAddTypeName.equalsIgnoreCase(typeName) && !typeName.equalsIgnoreCase("-")) {
                typeIndex.add(typeIndex.get((typeIndex.size() - 1) < 0 ? 0 : typeIndex.size() - 1) + 1 + typeCountList.get((typeCountList.size() - 1) < 0 ? 0 : typeCountList.size() - 1));//类别所在的位置
                typeCountList.add(count);
            }
            allLinks.addAll(classLinks);
        }
        for (int i = 0; i < typeList.size(); i++) {
            allLinks.add(typeIndex.get(i), new AndroidClassLinkBean(typeList.get(i), "", ""));
        }
        return this;
    }

    public List<String> getTypeList() {
        if (typeList == null)
            throw new IllegalStateException("请先调用 parse() 函数");
        return typeList;
    }

    public List<Integer> getTypeCountList() {
        if (typeCountList == null)
            throw new IllegalStateException("请先调用 parse() 函数");
        return typeCountList;
    }

    public List<Integer> getTypeIndex() {
        if (typeIndex == null)
            throw new IllegalStateException("请先调用 parse() 函数");
        return typeIndex;
    }


    /**
     * 判断当前位置,是否是分类类型数据
     *
     * @param pool     the pool
     * @param position the position
     * @return the boolean
     */
    public static boolean isClassType(DataPool pool, int position) {
        if (pool == null || position < 0) {
            return false;
        }
        if (pool.typeIndex.size() < 0) {
            return false;
        }
        for (Integer n : pool.typeIndex) {
            if (n == position) {
                return true;
            }
        }
        return false;
    }

    public static int getClassTypeIndex(DataPool pool, String classType) {
        if (pool == null || pool.typeIndex.size() < 0 || classType == null || classType.length() <= 0) {
            return -1;
        }

        for (int i = 0; i < pool.getTypeList().size(); i++) {
            if (classType.equalsIgnoreCase(pool.getTypeList().get(i))) {
                return i;
            }
        }

        return -1;
    }
}
