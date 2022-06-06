package KO.platform.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.scijava.nativelib.NativeLibraryUtil;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Symbol;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.arm.backend.KvmFactory;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.BaseVM;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.VarArg;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.github.unidbg.virtualmodule.android.AndroidModule;

import KO.utils.Color;
import unicorn.Arm64Const;
import unicorn.ArmConst;

public class AndroidMock {
	/**
	 * @return the emulator
	 */
	public final AndroidEmulator getEmulator() {
		return emulator;
	}

	/**
	 * @return the aPI
	 */
	public final DvmClass getAPI() {
		return API;
	}

	/**
	 * @return the vm
	 */
	public final VM getVm() {
		return vm;
	}

	/**
	 * @return the dm
	 */
	public final DalvikModule getDm() {
		return dm;
	}

	/**
	 * @return the dmLibc
	 */
	public final DalvikModule getDmLibc() {
		return dmLibc;
	}

	/**
	 * @return the cache
	 */
	public final StringObject getCache() {
		return cache;
	}

	/**
	 * @return the cache2
	 */
	public final DvmObject<?> getCache2() {
		return cache2;
	}

	/**
	 * @return the cache3
	 */
	public final StringObject getCache3() {
		return cache3;
	}

	/**
	 * @return the cache4
	 */
	public final DvmObject<?> getCache4() {
		return cache4;
	}

	/**
	 * @return the cache5
	 */
	public final HashMap<String, MemoryBlock> getCache5() {
		return cache5;
	}

	/**
	 * @return the memory
	 */
	public final Memory getMemory() {
		return memory;
	}

	/**
	 * @param dm the dm to set
	 */
	public final void setDm(DalvikModule dm) {
		this.dm = dm;
	}

	/**
	 * @param dmLibc the dmLibc to set
	 */
	public final void setDmLibc(DalvikModule dmLibc) {
		this.dmLibc = dmLibc;
	}

	/**
	 * @param cache the cache to set
	 */
	public final void setCache(StringObject cache) {
		this.cache = cache;
	}

	/**
	 * @param cache2 the cache2 to set
	 */
	public final void setCache2(DvmObject<?> cache2) {
		this.cache2 = cache2;
	}

	/**
	 * @param cache3 the cache3 to set
	 */
	public final void setCache3(StringObject cache3) {
		this.cache3 = cache3;
	}

	/**
	 * @param cache4 the cache4 to set
	 */
	public final void setCache4(DvmObject<?> cache4) {
		this.cache4 = cache4;
	}

	/**
	 * @param cache5 the cache5 to set
	 */
	public final void setCache5(HashMap<String, MemoryBlock> cache5) {
		this.cache5 = cache5;
	}

	/**
	 * @param memory the memory to set
	 */
	public final void setMemory(Memory memory) {
		this.memory = memory;
	}

	private AndroidEmulator emulator;

	private final DvmClass API;

	private final VM vm;

	private DalvikModule dm;

	private DalvikModule dmLibc;

	protected StringObject cache = null;

	protected DvmObject<?> cache2;

	protected StringObject cache3;

	protected DvmObject<?> cache4;

	protected HashMap<String, MemoryBlock> cache5 = new HashMap<String, MemoryBlock>();

	private static Consumer<String> log = a -> System.out.println("[" + Thread.currentThread().getName() + "] " + a);

	private Memory memory;

	public void clear() {
		getCache5().clear();

		memory = null;
		dm = null;
		dmLibc = null;
	}

	UnidbgPointer outPoi = null;

	protected int lengt;
	
	public AndroidMock() {

		log.accept(Color.ANSI_RESET + "Booting Unidbg...");

		AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false) {
			public AndroidEmulator build() {
				return new AndroidARMEmulator(processName, rootDir, backendFactories) {
				};
			}
		};

		cache5.clear();

		log.accept("using Unicorn2Factory");

		emulator = builder.addBackendFactory(new Unicorn2Factory(false))
				.setProcessName("com.cndatacom.campus.cdccportalgd").build();

		memory = emulator.getMemory();
		memory.setLibraryResolver(new AndroidResolver(23));

		vm = emulator.createDalvikVM();

		new AndroidModule(emulator, vm).register(memory);

		vm.setVerbose(false);
		vm.setJni(new AbstractJni() {
			@Override
			public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {

				switch (signature) {
				case "android/content/Context->getFilesDir()Ljava/io/File;":
					return cache2 != null ? cache2
							: (cache2 = vm.resolveClass("java/io/File").newObject(new File("target")));
				case "java/io/File->getAbsolutePath()Ljava/lang/String;":
					if (cache == null) {
						File file = (File) dvmObject.getValue();
						return cache = new StringObject(vm, file.getAbsolutePath());
					}
					return cache;
				}
				return super.callObjectMethod(vm, dvmObject, signature, varArg);
			}

			@Override
			public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {

				switch (signature) {
				case "android/app/ActivityThread->currentPackageName()Ljava/lang/String;":
					return cache3 != null ? cache3 : (cache3 = new StringObject(vm, "com.cndatacom.campus.netcore"));
				case "android/app/ActivityThread->currentApplication()Landroid/app/Application;":
					return cache4 != null ? cache4
							: (cache4 = vm.resolveClass("android/app/Application", vm.resolveClass(
									"android/content/ContextWrapper", vm.resolveClass("android/content/Context")))
									.newObject(signature));
				}

				return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);

			}
		});

		dmLibc = vm.loadLibrary(new File("libc.so"), true);

		dm = vm.loadLibrary(new File("libdaproxy.so"), true);

		long address = dmLibc.getModule().findSymbolByName("strcmp").getAddress();

		HookZz hook = HookZz.getInstance(emulator);

		hook.replace(address, new ReplaceCallback() {

			@Override
			public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
				String arg2 = context.getPointerArg(1).getString(0);

				List<String> Filter = Arrays.asList(new String[] { "ipv4", "local-time" });

				// yyyy-mm-dd 00:00:00
				Filter.forEach(key -> {
					if (arg2.indexOf(key) == 0) {

						if (!cache5.containsKey(key)) {
							MemoryBlock fakeInputBlock = emulator.getMemory().malloc(key.length(), true);

							fakeInputBlock.getPointer().write(key.getBytes(StandardCharsets.US_ASCII));

							cache5.put(key, fakeInputBlock);
						}

						emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, cache5.get(key).getPointer().peer);

					}
				});

				return HookStatus.RET(emulator, originFunction);

			}
		});

		dm.callJNI_OnLoad(emulator);

//		
//		 long sub_13558_address = dm.getModule().base+0x3c4c + 1;
//	        HookZz hooks = HookZz.getInstance(emulator);
//	        hooks.replace(sub_13558_address, new ReplaceCallback() {
//	        	private int a = 1;
//
//				     	  UnidbgPointer outPointer = null;
//	            @Override
//	            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
//
//	            	
//	            	if(++a == 2) {
//		                System.out.println(context.getIntArg(3) + "   ..." );
//	            	}
//	            		
//	            	
//
////	   //         	 outPointer = context.getPointerArg(0);
////	            	outPointer = context.getPointerArg(0);
////	                System.out.println(a + ",初始 3ecc 入参arg2:" + (lengt >>> 2));
////	                Inspector.inspect(outPointer.getByteArray(0, 88), "3ecc in");
//	                
//	                //-1073744704
//
//	     //           emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, -1073744704);
//	                return HookStatus.RET(emulator,originFunction);
//	            }
//
//	            @Override
//	            public void postCall(Emulator<?> emulator, HookContext context) {
//
//	     //       	 Inspector.inspect(outPointer.getByteArray(0, 88), "3ecc out");
//	    //            int rep = context.getIntArg(0);
//	    //            System.out.println(rep);
//	   //             System.out.println(",replace_sub_13558_arg 返回字符串结果:"+outPointer.getString(0));
////	                super.postCall(emulator, context);
//	            }
//	        },true);
//	        
////	        
////	        sub_13558_address = dm.getModule().base+0x3f08 + 1;
////	        hooks = HookZz.getInstance(emulator);
////	        hooks.replace(sub_13558_address, new ReplaceCallback() {
////	        	private int a;
////
////				     	  UnidbgPointer outPointer = null;
////	            @Override
////	            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
////
////	            	 outPointer = context.getPointerArg(0);
////	                System.out.println(a++ + "初始 3f08 入参arg2:");
////	                Inspector.inspect(outPointer.getByteArray(0, 88), "3f08");
////	                
////	                //-1073744704
////
////	       //         emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, -1073744704);
////	                return HookStatus.RET(emulator,originFunction);
////	            }
////
////	            @Override
////	            public void postCall(Emulator<?> emulator, HookContext context) {
////
////
////	     //           int rep = context.getIntArg(0);
////	    //            System.out.println(rep);
////	                System.out.println("3f08 返回字符串结果:");
////	                
////	                Inspector.inspect(outPointer.getByteArray(0, 88), "3f08 out");
////	                
//////	                super.postCall(emulator, context);
////	            }
////	        },true);
////	        
////	        
//	        sub_13558_address = dm.getModule().base+0x2E80 + 1;
//	        hooks = HookZz.getInstance(emulator);
//	        hooks.replace(sub_13558_address, new ReplaceCallback() {
//	        	
//				     	  
//	            @Override
//	            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
//
//	            	 ;
//	            	 int len = context.getPointerArg(3).getInt(0);
//	      //      	 lengt = context.getIntArg(3);
//	                System.out.println(lengt + "初始 3c4c 入参arg2:");
//	                writeByte(context.getPointerArg(4).getByteArray(0, len));
//	                
//	                //-1073744704
//
//	       //         emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, -1073744704);
//	                return HookStatus.RET(emulator,originFunction);
//	            }
//
//	            @Override
//	            public void postCall(Emulator<?> emulator, HookContext context) {
//
//
//	     //           int rep = context.getIntArg(0);
//	    //            System.out.println(rep);
//	                System.out.println("3c4c 返回字符串结果:");
//	                
//	 //               Inspector.inspect(outPoi.getByteArray(0, lengt), "3c4c out");
//	                
////	                super.postCall(emulator, context);
//	            }
//	        },true);
//
//	        
//
////			MemoryBlock initial_msg = emulator.getMemory().malloc(4, false);
////			UnidbgPointer initial_msg_ptr = initial_msg.getPointer();
////	//
//////			// 将参数1写入 -1073744132
////			initial_msg_ptr.write(new byte[] {0x0,0x0, 0x0 , 0});
////
////			UnidbgPointer outPutStream = memory.allocateStack(4);
////			
////			outPutStream.write(new byte[] {0x1,0x1, 0x1 , 0});
////
////			System.out.println(initial_msg_ptr.getInt(0));
////			
////			
////			emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, initial_msg_ptr.peer);
////			
////			Number ret = dm.getModule().callFunction(emulator, 0x3ECC + 1, outPutStream);
////			System.out.println(ret.intValue());
////			
////			System.out.println(initial_msg_ptr.getInt(0));
		API = vm.resolveClass("com/cndatacom/campus/netcore/DaMod");
	}
	
	static void writeByte(byte[] bytes)
    {
 
        // Try block to check for exceptions
        try {
 
            // Initialize a pointer in file
            // using OutputStream
            OutputStream os = new FileOutputStream("outx", false);
 
            // Starting writing the bytes in it
            os.write(bytes);
 
            // Display message onconsole for successful
            // execution

 
            // Close the file connections
            os.close();
        }
 
        // Catch block to handle the exceptions
        catch (Exception e) {
 
            // Display exception on console
            System.out.println("Exception: " + e);
        }
    }
}
