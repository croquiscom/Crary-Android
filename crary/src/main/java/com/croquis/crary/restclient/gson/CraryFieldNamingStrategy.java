package com.croquis.crary.restclient.gson;

import com.google.gson.FieldNamingStrategy;

import java.lang.reflect.Field;
import java.util.Locale;

public class CraryFieldNamingStrategy implements FieldNamingStrategy {
	@Override
	public String translateName(Field f) {
		String name = f.getName();
		StringBuilder spans = new StringBuilder();
		int i = 0;
		if (name.charAt(0) == 'm' && Character.isUpperCase(name.charAt(1))) {
			// ignore first m that means member
			i = 1;
		}
		for (; i < name.length(); i++) {
			char character = name.charAt(i);
			if (Character.isUpperCase(character) && spans.length() != 0) {
				spans.append("_");
			}
			spans.append(character);
		}
		return spans.toString().toLowerCase(Locale.US);
	}
}
