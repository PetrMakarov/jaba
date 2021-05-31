// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package com.pmakarov.jabaui.style.apply.jbutton.ui;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

@Data
@EqualsAndHashCode(callSuper = true)
public final class JabaButtonUI extends BasicButtonUI {

    private double widthRadius = 0d;
    private double heightRadius = 0d;
    private double borderWidth = 1d;

    //hover
    private JabaButtonUIState hover = new JabaButtonUIState("#000000", "#000000", "#FFFFFF");
    //pressed
    private JabaButtonUIState pressed = new JabaButtonUIState("#000000", "#000000", "#FFFFFF");
    //normal
    private JabaButtonUIState normal = new JabaButtonUIState("#000000", "#000000", "#FFFFFF");

    private String buttonText;

    private Shape shape;
    private Shape border;
    private Shape base;

    public JabaButtonUI() {
        this(0d);
    }

    public JabaButtonUI(Number radius) {
        if (null != radius) {
            widthRadius = radius.doubleValue();
            heightRadius = radius.doubleValue();
        }
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setBackground(normal.getBackgroundColor());
        b.setForeground(normal.getTextColor());
        b.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        initShape(b);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        initShape(c);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (model.isArmed()) { // pressed
                // border
                g2.setPaint(pressed.getBorderColor());
                g2.fill(border);
                // background
                g2.setPaint(pressed.getBackgroundColor());
                // text
                c.setForeground(pressed.getTextColor());
                g2.fill(shape);
            } else if (b.isRolloverEnabled() && model.isRollover()) { //hover
                // border
                g2.setPaint(hover.getBorderColor());
                g2.fill(border);
                // background
                g2.setPaint(hover.getBackgroundColor());
                //text
                c.setForeground(hover.getTextColor());
                g2.fill(shape);
            } else {
                // border
                g2.setPaint(normal.getBorderColor());
                g2.fill(border);
                // background
                g2.setPaint(normal.getBackgroundColor());
                // text
                c.setForeground(normal.getTextColor());
                g2.fill(shape);
            }
        }

        g2.dispose();
        super.paint(g, c);
    }

    public boolean contains(int x, int y) {
        return shape.contains(x, y);
    }

    protected void initShape(Component c) {
        if (!c.getBounds().equals(base)) {
            base = c.getBounds();
            border = new RoundRectangle2D.Double(
                    0d, 0d,
                    c.getWidth() - 1d,
                    c.getHeight() - 1d,
                    widthRadius, heightRadius);
            shape = new RoundRectangle2D.Double(
                    borderWidth, borderWidth,
                    c.getWidth() - 1d - borderWidth * 2d,
                    c.getHeight() - 1d - borderWidth * 2d,
                    widthRadius, heightRadius);
        }
    }

}
