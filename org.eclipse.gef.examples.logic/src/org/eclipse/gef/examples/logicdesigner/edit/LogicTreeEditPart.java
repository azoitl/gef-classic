/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.logicdesigner.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import org.eclipse.gef.EditPolicy;

import org.eclipse.gef.examples.logicdesigner.model.LED;
import org.eclipse.gef.examples.logicdesigner.model.LogicElement;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;

/**
 * EditPart for Logic components in the Tree.
 */
public class LogicTreeEditPart extends org.eclipse.gef.editparts.AbstractTreeEditPart
		implements PropertyChangeListener {

	/**
	 * Constructor initializes this with the given model.
	 *
	 * @param model Model for this.
	 */
	public LogicTreeEditPart(Object model) {
		super(model);
	}

	@Override
	public void activate() {
		super.activate();
		getLogicSubpart().addPropertyChangeListener(this);
	}

	/**
	 * Creates and installs pertinent EditPolicies for this.
	 */
	@Override
	protected void createEditPolicies() {
		EditPolicy component;
		if (getModel() instanceof LED) {
			component = new LEDEditPolicy();
		} else {
			component = new LogicElementEditPolicy();
		}
		installEditPolicy(EditPolicy.COMPONENT_ROLE, component);
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new LogicTreeEditPolicy());
	}

	@Override
	public void deactivate() {
		getLogicSubpart().removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Returns the model of this as a LogicSubPart.
	 *
	 * @return Model of this.
	 */
	protected LogicSubpart getLogicSubpart() {
		return (LogicSubpart) getModel();
	}

	/**
	 * Returns <code>null</code> as a Tree EditPart holds no children under it.
	 *
	 * @return <code>null</code>
	 */
	@Override
	protected List<LogicElement> getModelChildren() {
		return Collections.emptyList();
	}

	@Override
	public void propertyChange(PropertyChangeEvent change) {
		if (change.getPropertyName().equals(LogicElement.CHILDREN)) {
			if (change.getOldValue() instanceof Integer intVal) {
				// new child
				addChild(createChild(change.getNewValue()), intVal.intValue());
			} else {
				// remove child
				removeChild(getViewer().getEditPartForModel(change.getOldValue()));
			}
		} else {
			refreshVisuals();
		}
	}

	/**
	 * Refreshes the visual properties of the TreeItem for this part.
	 */
	@Override
	protected void refreshVisuals() {
		if (getWidget() instanceof Tree) {
			return;
		}
		Image image = getLogicSubpart().getIcon();
		TreeItem item = (TreeItem) getWidget();
		if (image != null) {
			image.setBackground(item.getParent().getBackground());
		}
		setWidgetImage(image);
		setWidgetText(getLogicSubpart().toString());
	}

}
