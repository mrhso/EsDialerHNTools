package KO.platform.android;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.backend.KvmFactory;
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
import com.github.unidbg.virtualmodule.android.AndroidModule;

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

	private final AndroidEmulator emulator;

	private final DvmClass API;

	private final VM vm;
	
	private DalvikModule dm;

	private DalvikModule dmLibc;
	
	protected StringObject cache = null;

	protected DvmObject<?> cache2;

	protected StringObject cache3;

	protected DvmObject<?> cache4;

	protected HashMap<String, MemoryBlock> cache5 = new HashMap<String, MemoryBlock>();
	
	private static Consumer<String> log = a -> System.out.println(a);

	private Memory memory;
	
	
	public void clear() {
		getCache5().clear();
		
		memory = null;
		dm = null;
		dmLibc = null;
	}

	public AndroidMock() {
		
		log.accept("Booting Unidbg...");
		
		AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false) {
			public AndroidEmulator build() {
				return new AndroidARMEmulator(processName, rootDir, backendFactories) {
				};
			}
		};

		cache5.clear();

		emulator = builder
//				.addBackendFactory(new HypervisorFactory(true))
				.addBackendFactory(new KvmFactory(true)).setProcessName("com.cndatacom.campus.cdccportalgd").build();

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

		API = vm.resolveClass("com/cndatacom/campus/netcore/DaMod");
	}
}
