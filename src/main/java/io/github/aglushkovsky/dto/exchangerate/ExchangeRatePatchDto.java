package io.github.aglushkovsky.dto.exchangerate;

public class ExchangeRatePatchDto {
    private String rate;

    public ExchangeRatePatchDto(String rate) {
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
