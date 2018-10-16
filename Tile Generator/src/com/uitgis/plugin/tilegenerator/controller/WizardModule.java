package com.uitgis.plugin.tilegenerator.controller;

import com.google.inject.AbstractModule;
import com.uitgis.plugin.tilegenerator.model.WizardData;


public class WizardModule extends AbstractModule {
    @Override
    protected void configure() {
        WizardData model = new WizardData();
        bind(WizardData.class).toInstance(model);
    }
}
