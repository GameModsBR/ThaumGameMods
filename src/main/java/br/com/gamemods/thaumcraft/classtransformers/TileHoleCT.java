package br.com.gamemods.thaumcraft.classtransformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntity;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import thaumcraft.common.Thaumcraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TileHoleCT implements IClassTransformer
{
    public static void blockSparkle(TileEntity te)
    {
        Thaumcraft.proxy.blockSparkle(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, 4194368, 1);
    }

    private class Generator extends GeneratorAdapter
    {
        protected Generator(MethodVisitor mv, int access, String name, String desc)
        {
            super(Opcodes.ASM4, mv, access, name, desc);
        }

        @Override
        public void visitInsn(int opcode)
        {
            if(opcode == Opcodes.RETURN)
            {
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, TileHoleCT.class.getName().replace('.','/'),
                        "blockSparkle", "(Lnet/minecraft/tileentity/TileEntity;)V");
            }

            super.visitInsn(opcode);
        }
    }

    @Override
    public byte[] transform(String s, String srgName, byte[] bytes)
    {
        if(srgName.equals("thaumcraft.common.tiles.TileHole"))
        {
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, Opcodes.ASM4);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if("readCustomNBT".equals(name))
                        return new Generator(mv, access, name, desc);

                    return mv;
                }
            };

            reader.accept(visitor, ClassReader.EXPAND_FRAMES);

            bytes = writer.toByteArray();
            FileOutputStream out = null;
            try
            {
                out = new FileOutputStream(new File(srgName+".class"));
                out.write(bytes);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (out != null)
                    {
                        out.close();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}
