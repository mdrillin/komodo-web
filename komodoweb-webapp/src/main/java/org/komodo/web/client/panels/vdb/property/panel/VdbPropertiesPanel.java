/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.web.client.panels.vdb.property.panel;

import org.komodo.web.client.panels.vdb.property.PropertiesPanelDescriptor;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties panel for the top-level vdb node
 */
@UiTemplate("./VdbPropertiesPanel.ui.xml")
public class VdbPropertiesPanel extends AbstractPropertiesPanel {

    /**
     * Descriptor for this panel
     */
    public static class Descriptor extends PropertiesPanelDescriptor<VdbPropertiesPanel> {

        /**
         * Create new instance
         */
        public Descriptor() {
            super(VdbPropertiesPanel.class.getName());
        }

        @Override
        public VdbPropertiesPanel create(double parentWidth, double parentHeight) {
            return new VdbPropertiesPanel(parentWidth, parentHeight);
        }

        @Override
        public void setContent(VdbPropertiesPanel panel, KomodoObjectBean kObject) {
            panel.setKomodoObject(kObject);
        }
    }

    interface VdbPropertiesPanelUiBinder extends UiBinder<Widget, VdbPropertiesPanel> {
        // Nothing required
    }

    private static VdbPropertiesPanelUiBinder uiBinder = GWT.create(VdbPropertiesPanelUiBinder.class);

    @UiField
    TextBox nameBox;

    @UiField
    TextBox versionBox;

    @UiField
    TextArea descriptionArea;

    @UiField
    CheckBox previewBox;

    /**
     * Create new instance
     *
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    protected VdbPropertiesPanel(double parentWidth, double parentHeight) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private String previewProperty() {
        return VdbLexicon.Vdb.PREVIEW;
    }

    private String descriptionProperty() {
        return VdbLexicon.Vdb.DESCRIPTION;
    }

    private String versionProperty() {
        return VdbLexicon.Vdb.VERSION;
    }

    private String nameProperty() {
        return VdbLexicon.Vdb.NAME;
    }

    @Override
    protected void update(KomodoObjectBean kObject) {
        if (kObject == null)
            return;

        String propertyName = nameProperty();
        nameBox.setText(getValueAsString(kObject.getProperty(propertyName)));

        propertyName = versionProperty();
        versionBox.setText(getValueAsString(kObject.getProperty(propertyName)));

        propertyName = descriptionProperty();
        descriptionArea.setText(getValueAsString(kObject.getProperty(propertyName)));

        propertyName = previewProperty();
        previewBox.setValue(getValueAsBoolean(kObject.getProperty(propertyName)));
    }

    @UiHandler("nameBox")
    protected void onNameBoxBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

        String nameProperty = nameProperty();
        updateProperty(nameProperty, nameBox.getText());
    }

    @UiHandler("versionBox")
    protected void onVersionBoxBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

      String versionProperty = versionProperty();
      updateProperty(versionProperty, versionBox.getText());
    }

    @UiHandler("descriptionArea")
    protected void onDescriptionAreaBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

      String descProperty = descriptionProperty();
      updateProperty(descProperty, descriptionArea.getText());
    }

    @UiHandler("previewBox")
    protected void onPreviewBoxBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

      String previewProperty = previewProperty();
      updateProperty(previewProperty, previewBox.getValue());
    }
}
