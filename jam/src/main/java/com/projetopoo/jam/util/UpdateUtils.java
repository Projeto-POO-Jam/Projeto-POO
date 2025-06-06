package com.projetopoo.jam.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UpdateUtils {
    /**
     * Copia as propriedades não nulas de um objeto de origem para um objeto de destino.
     *
     * @param source o objeto de origem
     * @param destination o objeto de destino
     * @param protectedFields uma lista de nomes de campos que NUNCA devem ser copiados.
     */
    public static void copyNonNullProperties(Object source, Object destination, String... protectedFields) {
        String[] ignoredProperties = getNullAndProtectedPropertyNames(source, protectedFields);

        BeanUtils.copyProperties(source, destination, ignoredProperties);
    }

    private static String[] getNullAndProtectedPropertyNames(Object source, String... protectedFields) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        if (protectedFields != null) {
            emptyNames.addAll(Arrays.asList(protectedFields));
        }

        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
