package Test;

import java.time.LocalDate;

/**
 * Modelo de datos: Factura
 */
public class Factura {

    public enum Estado { PENDIENTE, PAGADA, ANULADA }

    private int    id;
    private String paciente;
    private String servicio;
    private double subtotal;
    private double descuento;
    private double iva;
    private double total;
    private String fecha;
    private Estado estado;
    private String metodoPago;

    public Factura(int id, String paciente, String servicio,
                   double subtotal, double descuento, double iva,
                   String fecha, Estado estado, String metodoPago) {
        this.id          = id;
        this.paciente    = paciente;
        this.servicio    = servicio;
        this.subtotal    = subtotal;
        this.descuento   = descuento;
        this.iva         = iva;
        this.total       = (subtotal - descuento) + ((subtotal - descuento) * iva / 100.0);
        this.fecha       = fecha;
        this.estado      = estado;
        this.metodoPago  = metodoPago;
    }

    public int    getId()        { return id; }
    public String getPaciente()  { return paciente; }
    public String getServicio()  { return servicio; }
    public double getSubtotal()  { return subtotal; }
    public double getDescuento() { return descuento; }
    public double getIva()       { return iva; }
    public double getTotal()     { return total; }
    public String getFecha()     { return fecha; }
    public Estado getEstado()    { return estado; }
    public String getMetodoPago(){ return metodoPago; }

    public void setEstado(Estado e) { this.estado = e; }
}
