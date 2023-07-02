package aopTest.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"SameParameterValue","unused","UnusedReturnValue"})
public class Util {
    /**
     * 得到当前的时间，格式为YYYYmmDD
     * @return Long 八位数字
     */
    public Long getNowDate() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sp.format(cd.getTime());
        return Long.parseLong(nowDay);
    }

    /**
     * 检查实体中是否存在某字段
     * @param t 实体对象
     * @param fieldName 字段名
     * @param <T> 实体类型
     * @return 存在则返回true,反之则返回false
     */
    private <T> boolean checkFieldExist(T t, String fieldName) {
        Class clazz = t.getClass();
        List<Field> fields = getFieldContainSuper(clazz);
        for (Field field : fields)
            if (field.getName().equalsIgnoreCase(fieldName))
                return true;
        return false;
    }

    /**
     * 判断列表是否为空
     * @param list 列表对象
     * @return 列表是否为空
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 得到实体中某字段的值
     * @param t 实体对象
     * @param fieldName 字段名
     * @param <T> 实体类型
     * @return 字段值
     */
    public <T> Object getEntityFieldValue(T t, String fieldName) throws Exception {
        List<Field> fields = getFieldContainSuper(t.getClass());
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                try {
                    Method getter = getMethodByFieldName(fieldName, "get", t.getClass());
                    assert getter != null;
                    getter.setAccessible(true);
                    return getter.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new Exception(e.getMessage());
                }
            }
        }
        throw new Exception(String.format("对象%s中不存在%s字段的get方法", t.getClass().toString(), fieldName));
    }

    /**
     * 拷贝实体t1中非空字段给t2。
     * @param t1   实体1
     * @param t2   实体2
     * @param <T>  实体1类型
     * @param <T2> 实体2类型
     * @author hxl
     */
    public <T, T2> void copyProperty(T t1, T2 t2) {
        Class t_clazz = t1.getClass();
        Class t2_clazz = t2.getClass();
        Field[] fields = t_clazz.getDeclaredFields();
        Field[] fields2 = t2_clazz.getDeclaredFields();
        List<String> names = new ArrayList<>();
        for (Field field : fields2) {
            names.add(field.getName());
        }
        List<Field> fields1 = new ArrayList<>();
        for (Field field : fields) {
            String name = field.getName();
            if (names.contains(name))
                fields1.add(field);
        }
        for (Field field : fields1) {
            field.setAccessible(true);
            try {
                Object obj = field.get(t1);
                if (obj != null) {
                    String name = field.getName();
                    Field field1 = t2_clazz.getDeclaredField(name);
                    field1.setAccessible(true);
                    field1.set(t2, obj);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到类中所有的字段，不包括父类
     * @param clazz 类型
     * @return 字段列表
     */
    private List<Field> getFields(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        return new ArrayList<>(Arrays.asList(fields));
    }

    /**
     * 得到类中的所有字段，包括直接父类字段
     * @param clazz 类对象
     * @return 字段列表
     */
    private List<Field> getFieldContainSuper(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Field[] superField = clazz.getSuperclass().getDeclaredFields();
        List<Field> res = new ArrayList<>(Arrays.asList(fields));
        res.addAll(Arrays.asList(superField));
        return res;
    }

    /**
     * 得到类中所有字段，包括远亲
     * @param clazz 向上追溯到Object对象的父类中所有的字段
     * @return 字段列表
     */
    public static List<Field> getAllField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> res = new ArrayList<>(Arrays.asList(fields));
        Class superClass = clazz.getSuperclass();
        while (superClass != null) {
            Field[] superField = superClass.getDeclaredFields();
            res.addAll(Arrays.asList(superField));
            superClass = superClass.getSuperclass();
        }
        return res;
    }

    /**
     * 得到类中所有方法，不包括父类
     * @param clazz 类型
     * @return 方法列表
     */
    public static List<Method> getMethods(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        return new ArrayList<>(Arrays.asList(methods));
    }

    /**
     * 得到类中所有方法，包括直接父类
     * @param clazz 类型
     * @return 方法列表
     */
    private List<Method> getMethodContainSuper(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        Method[] parentMethods = clazz.getSuperclass().getDeclaredMethods();
        ArrayList<Method> methodList = new ArrayList<>(Arrays.asList(methods));
        methodList.addAll(Arrays.asList(parentMethods));
        return methodList;
    }

    /**
     * 得到类中所有的方法，包括直接父类和间接父类。
     * @param clazz 类型
     * @return 方法列表
     */
    public static List<Method> getAllMethod(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> res = new ArrayList<>(Arrays.asList(methods));
        Class superClass = clazz.getSuperclass();
        while (superClass != null) {
            Method[] superMethods = superClass.getDeclaredMethods();
            res.addAll(Arrays.asList(superMethods));
            superClass = superClass.getSuperclass();
        }
        return res;
    }

    /**
     * 获得对象中某字段的getter方法。
     * 在调用前，需先确保该对象包含此方法。
     * @param fieldName 字段名
     * @param clazz 类型
     * @return get方法
     */
    private <T> Method getSetMethodByFieldName(String fieldName, Class<T> clazz) {
        return getMethodByFieldName(fieldName, "set", clazz);
    }

    /**
     * 根据字段名和方法前缀获取对应的方法
     * @param fieldName 文件名
     * @param methodPrefix 方法前缀：get or set
     * @param clazz 类对象
     * @return methodPrefix+FieldName方法
     */
    private <T> Method getMethodByFieldName(String fieldName, String methodPrefix, Class<T> clazz) {
        try {
            String methodName = methodPrefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Field field = clazz.getDeclaredField(fieldName);
            Method method;
            if (methodPrefix.startsWith("get")) {
                method = clazz.getDeclaredMethod(methodName);
            } else
                method = clazz.getDeclaredMethod(methodName, field.getType());
            // 不建议在此方法里面执行setAccwssible，不利于养成良好的编程习惯
            // 但是不加在这里的话，外层函数就要写try/catch语句 是代码不美观
            // method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | NoSuchFieldException e) {
//            throw new BusinessException(String.format("%s对象中不存在%s字段对应的%s方法",clazz.toString(),fieldName,methodPrefix));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Map中的键值对填充进Entity中
     * @param map <String,Object>键值对
     * @param queryClass Entity类型
     * @param <T> 类型T
     * @return Entity对象
     */
    public <T> T setQueryDTO(Map<String, Object> map, Class<T> queryClass) {
        List<Field> fields = getFieldContainSuper(queryClass);
        T t = null;
        try {
            t = queryClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equalsIgnoreCase(key)) {
                    try {
                        Method method = getSetMethodByFieldName(fieldName, queryClass);
                        method.setAccessible(true);
                        method.invoke(t, value);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return t;
    }
}
