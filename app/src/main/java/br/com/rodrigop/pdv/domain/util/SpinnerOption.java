package br.com.rodrigop.pdv.domain.util;

/**
 * Created by android on 12/03/2016.
 */
public class SpinnerOption {
    private String codigoDeBarras;
    private String descricao;

    public SpinnerOption(String codigoDeBarras, String descricao) {
        this.codigoDeBarras = codigoDeBarras;
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
