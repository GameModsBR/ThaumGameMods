package br.com.gamemods.thaumcraft.classtransformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import thaumcraft.common.tiles.TileHole;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BlockHoleCT implements IClassTransformer
{
    TileHole create()
    {
        return new TileHole();
    }

    @Override
    public byte[] transform(String s, String srgName, byte[] bytes)
    {
        if(srgName.equals("thaumcraft.common.blocks.BlockHole"))
        {
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if("func_149915_a".equals(name) || "createNewTileEntity".equals(name))
                    {
                        GeneratorAdapter generator = new GeneratorAdapter(mv, access, name, desc);
                        generator.visitTypeInsn(Opcodes.NEW, "thaumcraft/common/tiles/TileHole");
                        generator.visitInsn(Opcodes.DUP);
                        generator.visitMethodInsn(Opcodes.INVOKESPECIAL, "thaumcraft/common/tiles/TileHole", "<init>", "()V");
                        generator.visitInsn(Opcodes.ARETURN);
                        generator.visitEnd();
                        return generator;
                    }

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
