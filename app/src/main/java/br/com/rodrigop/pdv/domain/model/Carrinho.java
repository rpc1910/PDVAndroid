package br.com.rodrigop.pdv.domain.model;

import java.io.Serializable;

/**
 * Created by rodrigo on 19/03/16.
 */
public class Carrinho implements Cloneable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1382880549600149967L;
    /**
     * Persistent Instance variables. This data is directly
     * mapped to the columns of database table.
     */
    //private int id;
    private String idCompra;
    private int encerrada;
    private int enviada;

    public int getEncerrada() {
        return encerrada;
    }

    public void setEncerrada(int encerrada) {
        this.encerrada = encerrada;
    }

    public int getEnviada() {
        return enviada;
    }

    public void setEnviada(int enviada) {
        this.enviada = enviada;
    }


    public Carrinho () {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }
}