package io.github.aglushkovsky.dto.currency;

public class CurrencyResponseDto {
    private int id;
    private String name;
    private String code;
    private String sign;

    public CurrencyResponseDto(int id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
