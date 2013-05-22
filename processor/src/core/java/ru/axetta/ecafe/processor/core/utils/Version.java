package ru.axetta.ecafe.processor.core.utils;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.04.13
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class Version implements Comparable<Version> {

    private Integer[] version;

    public Version(final String currentVersion) {
        String[] temp = currentVersion.split("[.]");
        version  = new Integer[temp.length];
        for (int i=0; i<temp.length; i++){
              version[i] = Integer.parseInt(temp[i]);
        }
    }

    public Integer[] getVersion() {
        return version;
    }

    @Override
    public int compareTo(Version v) {
        int result=0;
        int min = (v.getVersion().length>version.length?version.length:v.getVersion().length);
        for (int i=0;i<min;++i) {
            if(v.getVersion()[i] < this.version[i]){
                result = -1;
                break;
            }
            if(v.getVersion()[i] > this.version[i]){
                result = 1;
                break;
            }
        }
        if(result==0 && v.getVersion().length!=version.length){
            result = (v.getVersion().length>version.length?1:-1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i: version){
            stringBuilder.append(i);
            stringBuilder.append(".");
        }
        if(stringBuilder.length()>0) stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }
}
