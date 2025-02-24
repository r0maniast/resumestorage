package ru.javawebinar.basejava;

public class MainString {
    public static void main(String[] args) {
        String[] stringArray = new String[]{"1", "2", "3", "4", "5"};
//        String result = "";
        StringBuilder sb = new StringBuilder();
        for(String str: stringArray){
            sb.append(str).append(", ");
        }
        System.out.println(sb);
    }
}
