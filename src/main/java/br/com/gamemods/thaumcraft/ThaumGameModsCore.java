package br.com.gamemods.thaumcraft;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class ThaumGameModsCore implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{
            "br.com.gamemods.thaumcraft.classtransformers.ItemFocusPortableHoleCT",
            "br.com.gamemods.thaumcraft.classtransformers.BlockHoleCT",
            "br.com.gamemods.thaumcraft.classtransformers.TileHoleCT"
        };
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
