package tech.alexnijjar.golemoverhaul.client.compat.rei.neoforge;

import me.shedaniel.rei.forge.REIPluginClient;
import tech.alexnijjar.golemoverhaul.client.compat.rei.GolemOverhaulReiPlugin;

/**
 * This class is required to be present in order for the REI plugin to be loaded.
 * It doesn't do anything, but it's required.
 * <p>
 * REI is dumb that it does not include this in common as there is literally no reason for it to not be this just makes
 * it so that you need to have a dummy class in the forge package to make it work.
 */
@REIPluginClient
public class ReiIsDumbClientPlugin extends GolemOverhaulReiPlugin {}
