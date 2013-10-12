/*
 * Copyright (C) 2012-2013 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jnr.udis86;

import com.kenai.jffi.MemoryIO;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 *
 */
public final class X86Disassembler {


    public enum Syntax { INTEL, ATT }

    public enum Mode { I386, X86_64 }

    private final UDis86 udis86;
    private final libudis86 libudis86;
    final long ud;


    static final class SingletonHolder {
        static final UDis86 INSTANCE;
        static final Collection<String> SEARCH_PATHS = Arrays.asList("/usr/local/lib", "/opt/local/lib", "/usr/lib");
        static {
            NativeLibrary nativeLibrary = new NativeLibrary(Arrays.asList("udis86"), SEARCH_PATHS);
            UDis86 udis86 = null;
            try {
                nativeLibrary.findSymbolAddress("ud_init");
                udis86 = new UDis86();
                udis86.intel = nativeLibrary.getSymbolAddress("ud_translate_intel");
                udis86.att = nativeLibrary.getSymbolAddress("ud_translate_att");
                udis86.lib = new ReflectionLibraryLoader().loadLibrary(nativeLibrary, libudis86.class, Collections.emptyMap());
            } catch (UnsatisfiedLinkError ule) {
            }
            INSTANCE = udis86;
        }
    }

    public static boolean isAvailable() {
        try {
            return SingletonHolder.INSTANCE != null;
        } catch (Throwable ex) {
            return false;
        }
    }

    public static X86Disassembler create() {
        X86Disassembler dis = new X86Disassembler(SingletonHolder.INSTANCE);
        dis.init();
        dis.setSyntax(Syntax.INTEL);
        return dis;
    }

    private X86Disassembler(UDis86 udis86) {
        this.udis86 = udis86;
        this.libudis86 = udis86.lib;
        this.ud = MemoryIO.getInstance().allocateMemory(1024, true);
    }

    private void init() {
        libudis86.ud_init(this);
    }

    public void setMode(Mode mode) {
        libudis86.ud_set_mode(this, mode == Mode.I386 ? 32 : 64);
    }

    public void setInputBuffer(long buffer, int size) {
        libudis86.ud_set_input_buffer(this, buffer, size);
    }

    public void setSyntax(Syntax syntax) {
        libudis86.ud_set_syntax(this, syntax == Syntax.ATT ? udis86.att : udis86.intel);
    }

    public boolean disassemble() {
        return (libudis86.ud_disassemble(this) & 0xff) != 0;
    }

    public String insn() {
        return libudis86.ud_insn_asm(this);
    }

    public long offset() {
        return libudis86.ud_insn_off(this);
    }

    public static final class UDis86 {
        long intel;
        long att;
        libudis86 lib;
    }

    static interface libudis86 {
        void ud_init(X86Disassembler ud);
        void ud_set_mode(X86Disassembler ud, int mode);
        void ud_set_pc(X86Disassembler ud, @u_int64_t long pc);
        void ud_set_input_buffer(X86Disassembler ud, @intptr_t long data, @size_t long len);
        void ud_set_vendor(X86Disassembler ud, int vendor);
        void ud_set_syntax(X86Disassembler ud, @intptr_t long translator);
        void ud_input_skip(X86Disassembler ud, @size_t long size);
        int ud_input_end(X86Disassembler ud);
        int ud_decode(X86Disassembler ud);
        int ud_disassemble(X86Disassembler ud);
        String ud_insn_asm(X86Disassembler ud);
        @intptr_t long ud_insn_ptr(X86Disassembler ud);
        @u_int64_t long ud_insn_off(X86Disassembler ud);
        String ud_insn_hex(X86Disassembler ud);
        int ud_insn_len(X86Disassembler ud);
    }

    public static void main(String[] args) {
        X86Disassembler.create();
    }
}
