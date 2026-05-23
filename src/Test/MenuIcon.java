package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Íconos del menú lateral dibujados con Graphics2D.
 * No dependen de fuentes del sistema operativo — funcionan en Windows, Mac y Linux.
 */
public class MenuIcon implements Icon {

    public enum Tipo {
        // Menú lateral
        DASHBOARD, PACIENTES, CITAS, HISTORIA, FACTURACION, REPORTES, CERRAR_SESION,
        // Botones de acción
        EDITAR, ELIMINAR, VER, CONFIRMAR, CANCELAR, COMPLETAR,
        ACTUALIZAR, NUEVA, GUARDAR, ANULAR, PAGAR
    }

    private final Tipo   tipo;
    private final int    size;
    private final Color  color;

    public MenuIcon(Tipo tipo, int size, Color color) {
        this.tipo  = tipo;
        this.size  = size;
        this.color = color;
    }

    public Tipo getTipo() { return tipo; }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(color);
        g2.translate(x, y);

        float s = size;
        float p = s * 0.08f; // padding interno

        switch (tipo) {

            case DASHBOARD -> {
                // 4 cuadros en grid 2x2
                float w = (s - 3 * p) / 2;
                g2.fill(new RoundRectangle2D.Float(p,       p,       w, w, 2, 2));
                g2.fill(new RoundRectangle2D.Float(2*p+w,   p,       w, w, 2, 2));
                g2.fill(new RoundRectangle2D.Float(p,       2*p+w,   w, w, 2, 2));
                g2.fill(new RoundRectangle2D.Float(2*p+w,   2*p+w,   w, w, 2, 2));
            }

            case PACIENTES -> {
                // Silueta de persona: círculo (cabeza) + arco (cuerpo)
                float cr = s * 0.18f;  // radio cabeza
                float cx = s / 2;
                float cy = s * 0.30f;
                g2.fill(new Ellipse2D.Float(cx - cr, cy - cr, cr * 2, cr * 2));
                // Cuerpo como arco inferior
                float bw = s * 0.55f;
                float bh = s * 0.38f;
                float bx = cx - bw / 2;
                float by = s * 0.52f;
                g2.fill(new Arc2D.Float(bx, by, bw, bh, 0, 180, Arc2D.PIE));
            }

            case CITAS -> {
                // Calendario: rectángulo con líneas de días
                float bx = p, by = p * 2;
                float bw = s - 2 * p, bh = s - 3 * p;
                // Marco del calendario
                g2.setStroke(new BasicStroke(s * 0.07f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(bx, by, bw, bh, 3, 3));
                // Línea de encabezado
                float hy = by + bh * 0.28f;
                g2.drawLine((int)(bx), (int)hy, (int)(bx + bw), (int)hy);
                // Dos argollas superiores
                float rw = s * 0.07f;
                g2.setStroke(new BasicStroke(s * 0.09f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(bx + bw*0.28f), (int)(p*0.5f), (int)(bx + bw*0.28f), (int)(by + p));
                g2.drawLine((int)(bx + bw*0.72f), (int)(p*0.5f), (int)(bx + bw*0.72f), (int)(by + p));
                // Puntos de días
                g2.setStroke(new BasicStroke(1));
                float dotR = s * 0.055f;
                float row1 = hy + bh * 0.18f;
                float row2 = hy + bh * 0.42f;
                float row3 = hy + bh * 0.66f;
                float[] cols = {bx + bw*0.18f, bx + bw*0.42f, bx + bw*0.66f, bx + bw*0.90f};
                for (float col : cols) {
                    for (float row : new float[]{row1, row2, row3}) {
                        g2.fill(new Ellipse2D.Float(col - dotR, row - dotR, dotR*2, dotR*2));
                    }
                }
            }

            case HISTORIA -> {
                // Documento con líneas de texto
                float bx = p * 1.5f, by = p;
                float bw = s - 3 * p, bh = s - 2 * p;
                // Fondo del documento
                g2.setStroke(new BasicStroke(s * 0.07f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(bx, by, bw, bh, 3, 3));
                // Líneas de texto
                g2.setStroke(new BasicStroke(s * 0.08f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float lx1 = bx + bw * 0.15f;
                float lx2 = bx + bw * 0.85f;
                float lx3 = bx + bw * 0.60f;
                float gap = bh * 0.18f;
                float startY = by + bh * 0.28f;
                g2.drawLine((int)lx1, (int)(startY),        (int)lx2, (int)(startY));
                g2.drawLine((int)lx1, (int)(startY+gap),    (int)lx2, (int)(startY+gap));
                g2.drawLine((int)lx1, (int)(startY+gap*2),  (int)lx2, (int)(startY+gap*2));
                g2.drawLine((int)lx1, (int)(startY+gap*3),  (int)lx3, (int)(startY+gap*3));
                // Cruz médica pequeña en esquina superior
                float cx2 = bx + bw * 0.72f, cy2 = by + bh * 0.10f, cr2 = bw * 0.12f;
                g2.setStroke(new BasicStroke(s * 0.09f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(cx2), (int)(cy2 - cr2), (int)(cx2), (int)(cy2 + cr2));
                g2.drawLine((int)(cx2 - cr2), (int)(cy2), (int)(cx2 + cr2), (int)(cy2));
            }

            case FACTURACION -> {
                // Billete / tarjeta de crédito
                float bx = p * 0.5f, by = s * 0.22f;
                float bw = s - p, bh = s * 0.55f;
                g2.setStroke(new BasicStroke(s * 0.07f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(bx, by, bw, bh, 4, 4));
                // Banda magnética
                float bandY = by + bh * 0.25f;
                g2.setStroke(new BasicStroke(bh * 0.22f));
                g2.drawLine((int)(bx), (int)bandY, (int)(bx + bw), (int)bandY);
                // Símbolo $ centrado
                g2.setFont(new Font("Arial", Font.BOLD, (int)(s * 0.32f)));
                FontMetrics fm = g2.getFontMetrics();
                String signo = "$";
                int sw = fm.stringWidth(signo);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawString(signo, (int)(bx + bw/2 - sw/2), (int)(by + bh * 0.82f));
            }

            case REPORTES -> {
                // Gráfico de barras
                float baseY = s - p * 1.5f;
                float barW  = (s - 2*p) / 5f;
                float gap2  = barW * 0.35f;
                float bw2   = barW - gap2;

                float[] heights = { s*0.35f, s*0.60f, s*0.45f, s*0.75f };
                for (int i = 0; i < heights.length; i++) {
                    float bx2 = p + i * barW;
                    float by2 = baseY - heights[i];
                    g2.fill(new RoundRectangle2D.Float(bx2, by2, bw2, heights[i], 2, 2));
                }
                // Línea base
                g2.setStroke(new BasicStroke(s * 0.06f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)p, (int)baseY, (int)(s-p), (int)baseY);
            }

            case CERRAR_SESION -> {
                float bx = p, by = p;
                float bw = s * 0.55f, bh = s - 2*p;
                g2.setStroke(new BasicStroke(s * 0.08f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(bx+bw*0.5f), (int)by, (int)bx, (int)by);
                g2.drawLine((int)bx, (int)by, (int)bx, (int)(by+bh));
                g2.drawLine((int)bx, (int)(by+bh), (int)(bx+bw*0.5f), (int)(by+bh));
                float ax  = bx + bw * 0.35f;
                float ay  = by + bh / 2;
                float ax2 = s - p;
                g2.drawLine((int)ax, (int)ay, (int)ax2, (int)ay);
                float ap = s * 0.15f;
                g2.drawLine((int)ax2, (int)ay, (int)(ax2-ap), (int)(ay-ap));
                g2.drawLine((int)ax2, (int)ay, (int)(ax2-ap), (int)(ay+ap));
            }

            case EDITAR -> {
                // Lápiz
                float stroke = s * 0.08f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Cuerpo del lápiz (rectángulo rotado ~45°)
                float cx = s/2, cy = s/2;
                GeneralPath lapiz = new GeneralPath();
                lapiz.moveTo(p*1.5f, s-p*2f);
                lapiz.lineTo(p*2.5f, s-p*1f);
                lapiz.lineTo(s-p*1.5f, p*2.5f);
                lapiz.lineTo(s-p*2.5f, p*1.5f);
                lapiz.closePath();
                g2.draw(lapiz);
                // Punta
                g2.drawLine((int)(p*1.5f), (int)(s-p*2f), (int)p, (int)(s-p));
                // Línea de separación punta/goma
                g2.drawLine((int)(p*2.2f), (int)(s-p*2.8f), (int)(p*3.2f), (int)(s-p*1.8f));
            }

            case ELIMINAR -> {
                // Basurero
                float stroke = s * 0.08f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float bx2 = p*1.8f, by2 = p*2.5f;
                float bw2 = s-p*3.6f, bh2 = s-p*4f;
                // Cuerpo del bote
                g2.draw(new RoundRectangle2D.Float(bx2, by2, bw2, bh2, 2, 2));
                // Tapa
                g2.drawLine((int)(p*0.8f),(int)(p*2f),(int)(s-p*0.8f),(int)(p*2f));
                // Asa/tapa pequeña
                g2.drawLine((int)(s*0.35f),(int)(p*1.2f),(int)(s*0.65f),(int)(p*1.2f));
                g2.drawLine((int)(s*0.35f),(int)(p*1.2f),(int)(s*0.35f),(int)(p*2f));
                g2.drawLine((int)(s*0.65f),(int)(p*1.2f),(int)(s*0.65f),(int)(p*2f));
                // Líneas internas
                g2.drawLine((int)(s*0.37f),(int)(by2+bh2*0.2f),(int)(s*0.37f),(int)(by2+bh2*0.85f));
                g2.drawLine((int)(s*0.50f),(int)(by2+bh2*0.2f),(int)(s*0.50f),(int)(by2+bh2*0.85f));
                g2.drawLine((int)(s*0.63f),(int)(by2+bh2*0.2f),(int)(s*0.63f),(int)(by2+bh2*0.85f));
            }

            case VER -> {
                // Ojo
                float stroke = s * 0.08f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float cx2 = s/2, cy2 = s/2;
                float ew = s*0.82f, eh = s*0.48f;
                // Contorno del ojo
                GeneralPath ojo = new GeneralPath();
                ojo.moveTo(cx2 - ew/2, cy2);
                ojo.quadTo(cx2, cy2 - eh/2, cx2 + ew/2, cy2);
                ojo.quadTo(cx2, cy2 + eh/2, cx2 - ew/2, cy2);
                g2.draw(ojo);
                // Pupila
                float pr = s * 0.13f;
                g2.fill(new Ellipse2D.Float(cx2-pr, cy2-pr, pr*2, pr*2));
            }

            case CONFIRMAR -> {
                // Check / palomita
                float stroke = s * 0.11f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(p*1.5f),(int)(s*0.52f),(int)(s*0.40f),(int)(s-p*1.8f));
                g2.drawLine((int)(s*0.40f),(int)(s-p*1.8f),(int)(s-p*1.2f),(int)(p*1.5f));
            }

            case CANCELAR, ANULAR -> {
                // X
                float stroke = s * 0.11f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(p*1.8f),(int)(p*1.8f),(int)(s-p*1.8f),(int)(s-p*1.8f));
                g2.drawLine((int)(s-p*1.8f),(int)(p*1.8f),(int)(p*1.8f),(int)(s-p*1.8f));
            }

            case COMPLETAR -> {
                // Doble check
                float stroke = s * 0.09f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Primer check
                g2.drawLine((int)(p),(int)(s*0.52f),(int)(s*0.32f),(int)(s-p*1.8f));
                g2.drawLine((int)(s*0.32f),(int)(s-p*1.8f),(int)(s*0.60f),(int)(p*1.8f));
                // Segundo check desplazado
                g2.drawLine((int)(s*0.35f),(int)(s*0.52f),(int)(s*0.60f),(int)(s-p*1.8f));
                g2.drawLine((int)(s*0.60f),(int)(s-p*1.8f),(int)(s-p),(int)(p*1.8f));
            }

            case ACTUALIZAR -> {
                // Flecha circular (reload)
                float stroke = s * 0.09f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float cx2 = s/2, cy2 = s/2, r = s*0.34f;
                g2.draw(new Arc2D.Float(cx2-r, cy2-r, r*2, r*2, 60, 270, Arc2D.OPEN));
                // Punta de flecha
                float ap2 = s * 0.14f;
                float ax2 = cx2 + r*(float)Math.cos(Math.toRadians(60));
                float ay2 = cy2 - r*(float)Math.sin(Math.toRadians(60));
                g2.drawLine((int)ax2,(int)ay2,(int)(ax2-ap2),(int)(ay2-ap2*0.3f));
                g2.drawLine((int)ax2,(int)ay2,(int)(ax2+ap2*0.3f),(int)(ay2-ap2));
            }

            case NUEVA -> {
                // Signo +
                float stroke = s * 0.11f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine((int)(s/2),(int)(p*1.5f),(int)(s/2),(int)(s-p*1.5f));
                g2.drawLine((int)(p*1.5f),(int)(s/2),(int)(s-p*1.5f),(int)(s/2));
            }

            case GUARDAR -> {
                // Disquete / floppy
                float stroke = s * 0.07f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float bx2 = p, by2 = p, bw2 = s-2*p, bh2 = s-2*p;
                g2.draw(new RoundRectangle2D.Float(bx2, by2, bw2, bh2, 2, 2));
                // Zona de etiqueta
                g2.draw(new Rectangle2D.Float(bx2+bw2*0.15f, by2, bw2*0.55f, bh2*0.35f));
                // Zona de disco
                g2.draw(new RoundRectangle2D.Float(bx2+bw2*0.2f, by2+bh2*0.55f, bw2*0.6f, bh2*0.35f, 2, 2));
            }

            case PAGAR -> {
                // Signo $ con círculo
                float stroke = s * 0.08f;
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                float cx2 = s/2, cy2 = s/2, r = s*0.38f;
                g2.draw(new Ellipse2D.Float(cx2-r, cy2-r, r*2, r*2));
                g2.setFont(new Font("Arial", Font.BOLD, (int)(s*0.40f)));
                FontMetrics fm = g2.getFontMetrics();
                String signo = "$";
                int sw = fm.stringWidth(signo);
                g2.setStroke(new BasicStroke(1));
                g2.drawString(signo, (int)(cx2 - sw/2), (int)(cy2 + fm.getAscent()*0.38f));
            }
        }
        g2.dispose();
    }
}