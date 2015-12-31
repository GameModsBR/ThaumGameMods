package br.com.gamemods.thaumcraft.classtransformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ItemFocusPortableHoleCT implements IClassTransformer
{
    public static boolean isRemote(World world)
    {
        return world.isRemote;
    }

    private class Generator extends GeneratorAdapter
    {
        private boolean patched;
        protected Generator(MethodVisitor mv, int access, String name, String desc)
        {
            super(Opcodes.ASM4, mv, access, name, desc);
        }

        @Override
        public void visitVarInsn(int opcode, int var)
        {
            if(!patched)
            {
                super.visitVarInsn(Opcodes.ALOAD, 2);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, ItemFocusPortableHoleCT.class.getName().replace('.','/'),
                        "isRemote", "(Lnet/minecraft/world/World;)Z"
                );
                Label label = new Label();
                super.visitJumpInsn(Opcodes.IFEQ, label);
                super.visitVarInsn(Opcodes.ALOAD, 1);
                super.visitInsn(Opcodes.ARETURN);
                super.visitLabel(label);
                patched = true;
            }

            super.visitVarInsn(opcode, var);
        }
    }

    private class CreateHoleGenerator extends GeneratorAdapter
    {
        private boolean patched;
        protected CreateHoleGenerator(MethodVisitor mv, int access, String name, String desc)
        {
            super(Opcodes.ASM4, mv, access, name, desc);
        }

        @Override
        public void visitVarInsn(int opcode, int var)
        {
            if(!patched)
            {
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, ItemFocusPortableHoleCT.class.getName().replace('.','/'),
                        "isRemote", "(Lnet/minecraft/world/World;)Z"
                );
                Label label = new Label();
                super.visitJumpInsn(Opcodes.IFEQ, label);
                super.visitInsn(Opcodes.ICONST_0);
                super.visitInsn(Opcodes.IRETURN);
                super.visitLabel(label);
                patched = true;
            }

            super.visitVarInsn(opcode, var);
        }
    }

    @Override
    public byte[] transform(String s, String srgName, byte[] bytes)
    {
        if(srgName.equals("thaumcraft.common.items.wands.foci.ItemFocusPortableHole"))
        {
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, Opcodes.ASM4);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if("onFocusRightClick".equals(name))
                        return new Generator(mv, access, name, desc);
                    else if("createHole".equals(name))
                        return new CreateHoleGenerator(mv, access, name, desc);

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
