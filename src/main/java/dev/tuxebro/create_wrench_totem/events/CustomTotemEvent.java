package dev.tuxebro.create_wrench_totem.events;

import com.simibubi.create.AllItems;
import dev.tuxebro.create_wrench_totem.CreateWrenchTotem;
import dev.tuxebro.create_wrench_totem.networking.CustomTotemAniamtionPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = CreateWrenchTotem.MOD_ID)
public class CustomTotemEvent {
    private static final ConcurrentHashMap<LivingEntity, AtomicInteger> entitiesWithTotemParticles = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLevelTick(LivingDamageEvent.Pre event) {
        var entity = event.getEntity();
        var damage = event.getOriginalDamage();
        var health = entity.getHealth();

        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        boolean isAboutToDie = health - damage <= 0.0f;
        if (!isAboutToDie) return;

        var totemWrench = getTotem(entity);
        if (totemWrench == null) return;

        event.setNewDamage(0);
        totemWrench.shrink(1);

        entity.removeAllEffects();
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

        entitiesWithTotemParticles.put(entity, new AtomicInteger(-1));

        serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (!(entity instanceof ServerPlayer player)) return;
        PacketDistributor.sendToPlayer(player, new CustomTotemAniamtionPayload(new ItemStack(AllItems.WRENCH.asItem())));
    }

    private static @Nullable ItemStack getTotem(LivingEntity entity) {
        ItemStack supposedTotem = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        if (hasTotemTag(supposedTotem)) return supposedTotem;

        supposedTotem = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if (hasTotemTag(supposedTotem)) return supposedTotem;

        return null;
    }

    private static boolean hasTotemTag(ItemStack supposedTotem) {
        CustomData data = supposedTotem.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;

        CompoundTag nbt = data.copyTag();
        if (!nbt.contains("IsTotem")) return false;
        return nbt.getBoolean("IsTotem");
    }

    @SubscribeEvent
    public static void onTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (entitiesWithTotemParticles.isEmpty()) return;

        entitiesWithTotemParticles.forEach((player, atomicTicks)->{
            int ticks = atomicTicks.incrementAndGet();

            if (ticks >= 100) {
                entitiesWithTotemParticles.remove(player);
                return;
            }

            if (ticks % 2 != 0) return;

            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY(0.5D), player.getZ(),
                    12, 0.5D, 0.5D, 0.5D, 0.0D
            );
        });
    }
}
