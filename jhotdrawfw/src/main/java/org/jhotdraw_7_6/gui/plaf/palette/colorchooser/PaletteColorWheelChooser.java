/*
 * @(#)PaletteColorWheelChooser.java 
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw_7_6.gui.plaf.palette.colorchooser;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;

import org.jhotdraw_7_6.color.HSVColorSpace;
import org.jhotdraw_7_6.color.JColorWheel;
import org.jhotdraw_7_6.gui.plaf.palette.PaletteLookAndFeel;
import org.jhotdraw_7_6.gui.plaf.palette.PalettePanelUI;

/**
 * A HSB color chooser, which displays a hue/saturation color wheel, and a 
 * brightness slider.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PaletteColorWheelChooser extends AbstractColorChooserPanel implements UIResource {

    private JColorWheel colorWheel;
    private PaletteColorSliderModel ccModel = new PaletteColorSliderModel(new HSVColorSpace());
    private int updatingChooser;

    /**
     * Creates a new instance.
     */
    public PaletteColorWheelChooser() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        brightnessSlider = new javax.swing.JSlider();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 6, 6, 6));
        setLayout(new java.awt.BorderLayout());

        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        brightnessSlider.setPaintTicks(true);
        add(brightnessSlider, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    
    protected void buildChooser() {
        initComponents();
        setUI(PalettePanelUI.createUI(this));

        int textSliderGap = PaletteLookAndFeel.getInstance().getInt("ColorChooser.textSliderGap");
        if (textSliderGap != 0) {
            BorderLayout layout = (BorderLayout) getLayout();
            layout.setHgap(textSliderGap);
        }

        colorWheel = new JColorWheel();
        add(colorWheel);

        ccModel.configureSlider(2, brightnessSlider);
        brightnessSlider.setMaximum(200);


        colorWheel.setModel(ccModel);

        ccModel.addChangeListener(new ChangeListener() {

            
            public void stateChanged(ChangeEvent evt) {
                setColorToModel(ccModel.getColor());
            }
        });
    }

    
    public String getDisplayName() {
        return PaletteLookAndFeel.getInstance().getString("ColorChooser.colorWheel");
    }

    
    public javax.swing.Icon getLargeDisplayIcon() {
        return PaletteLookAndFeel.getInstance().getIcon("ColorChooser.colorWheelIcon");
    }

    
    public Icon getSmallDisplayIcon() {
        return getLargeDisplayIcon();
    }

    
    public void updateChooser() {
        updatingChooser++;
        ccModel.setColor(getColorFromModel());
        updatingChooser--;
    }

    public void setColorToModel(Color color) {
        if (updatingChooser == 0) {
            getColorSelectionModel().setSelectedColor(color);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider brightnessSlider;
    // End of variables declaration//GEN-END:variables
}
