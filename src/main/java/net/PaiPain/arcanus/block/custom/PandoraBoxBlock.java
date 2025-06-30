package net.PaiPain.arcanus.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction; // [INCREMENTADO]
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock; // [INCREMENTADO]
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition; // [INCREMENTADO]
import net.minecraft.world.level.block.state.properties.DirectionProperty; // [INCREMENTADO]
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.item.context.BlockPlaceContext; // [INCREMENTADO]
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class PandoraBoxBlock extends HorizontalDirectionalBlock { // [INCREMENTADO]

    public class EnchantedBookHelper {
        public static ItemStack createEnchantedBook(Enchantment enchantment, int level) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(enchantment, level), book);
            return book;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);

        switch (dir) {
            case NORTH -> {
                return Block.box(-9, 0, 1, 24, 20, 20);
            }
            case SOUTH -> {
                return Block.box(-9, 0, 1, 24, 20, 20);
            }
            case EAST -> {
                return Block.box(1, 0, -9, 20, 20, 24);
            }
            case WEST -> {
                return Block.box(1, 0, -9, 20, 20, 24);
            }
            default -> {
                return Block.box(-9, 0, 1, 24, 20, 20);
            }
        }
    }
// alteração feita!

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public PandoraBoxBlock(Properties copy) {
        super(BlockBehaviour.Properties.of().strength(2.0f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); // [INCREMENTADO]
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { // [INCREMENTADO]
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { // [INCREMENTADO]
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Remover o bloco
            level.removeBlock(pos, false);

            // Soltar partículas de desintegração
            ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    20, 0.3, 0.3, 0.3, 0.05);

            RandomSource random = level.getRandom();
            int event = random.nextInt(40);

            switch (event) {
                case 0 -> {
                    level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, Level.ExplosionInteraction.TNT);
                }
                case 1 -> {
                    for (int i = 0; i < 10; i++) {
                        Creeper creeper = EntityType.CREEPER.create(level);
                        if (creeper != null) {
                            creeper.setPos(
                                    pos.getX() + random.nextDouble() * 2 - 1,
                                    pos.getY() + 1,
                                    pos.getZ() + random.nextDouble() * 2 - 1
                            );
                            level.addFreshEntity(creeper);
                        }
                    }
                }
                case 2 -> {
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.DIAMOND.getDefaultInstance().copyWithCount(10));
                    level.addFreshEntity(drop);
                }
                case 3 -> {
                    ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.LEATHER.getDefaultInstance().copyWithCount(5));
                    level.addFreshEntity(drop);
                }
                case 4 -> {
                    level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                }
                case 5 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 60, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, 1));
                }
                case 6 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 45, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 45, 1));
                }
                case 7 -> {
                    double dx = pos.getX() + random.nextInt(41) - 20;
                    double dz = pos.getZ() + random.nextInt(41) - 20;
                    double dy = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int) dx, pos.getY(), (int) dz)).getY();
                    player.teleportTo(dx + 0.5, dy, dz + 0.5);
                }
                case 8 -> {
                    // Aldeão Misterioso Bob com trades especiais
                    Villager villager = EntityType.VILLAGER.create(level);
                    if (villager != null) {
                        villager.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        villager.setCustomName(Component.literal("Bob"));
                        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.LIBRARIAN));

                        // Criar trades personalizados
                        MerchantOffers offers = villager.getOffers();

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 1),
                                EnchantedBookHelper.createEnchantedBook(Enchantments.UNBREAKING, 1),
                                10, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 5),
                                new ItemStack(Items.NETHERITE_SCRAP),
                                5, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 8),
                                new ItemStack(Items.END_CRYSTAL),
                                3, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 3),
                                new ItemStack(Items.BOOKSHELF),
                                10, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 4),
                                EnchantedBookHelper.createEnchantedBook(Enchantments.MOB_LOOTING,2),
                                5, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 2),
                                new ItemStack(Items.GOLDEN_CARROT, 3),
                                10, 0, 0.05f
                        ));

                        offers.add(new MerchantOffer(
                                new ItemStack(Items.EMERALD, 6),
                                new ItemStack(Items.HEART_OF_THE_SEA),
                                2, 0, 0.05f
                        ));

                        level.addFreshEntity(villager);
                    }
                }


                case 9 -> {
                    // Chuva de Diamantes
                    for (int i = 0; i < 10; i++) {
                        double dx = pos.getX() + random.nextDouble() * 2 - 1;
                        double dz = pos.getZ() + random.nextDouble() * 2 - 1;
                        ItemEntity diamond = new ItemEntity(level, dx, pos.getY() + 10, dz,
                                Items.DIAMOND.getDefaultInstance());
                        level.addFreshEntity(diamond);
                    }
                }

                case 10 -> {
                    // Benção da Resistência
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 60, 1));
                }

                case 11 -> {
                    // Armadura dos Ancestrais
                    ItemEntity armor = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(),
                            Items.DIAMOND_CHESTPLATE.getDefaultInstance());
                    level.addFreshEntity(armor);
                }

                case 12 -> {
                    // Livro dos Encantamentos
                    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.setEnchantments(Map.of(Enchantments.MENDING, 1), enchantedBook);
                    ItemEntity bookDrop = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), enchantedBook);
                    level.addFreshEntity(bookDrop);
                }

                case 13 -> {
                    // Moeda da Sorte
                    ItemStack coin = Items.EMERALD.getDefaultInstance();
                    coin.setHoverName(Component.literal("Moeda da Sorte").withStyle(ChatFormatting.GOLD));
                    ItemEntity luckyCoin = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), coin);
                    level.addFreshEntity(luckyCoin);
                }

                case 14 -> {
                    // Dropa todos os itens aleatórios do inventário
                    for (int i = 0; i < player.getInventory().items.size(); i++) {
                        ItemStack stack = player.getInventory().items.get(i);
                        if (!stack.isEmpty()) {
                            ItemEntity drop = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
                            level.addFreshEntity(drop);
                            player.getInventory().items.set(i, ItemStack.EMPTY);
                        }
                    }
                }

                case 15 -> {
                    player.hurt(level.damageSources().magic(), 12.0f); // 6 corações
                    level.playSound(null, pos, SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0f, 0.5f);

                    // Summonar um raio em cima do jogador
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        lightning.setPos(player.getX(), player.getY(), player.getZ());
                        level.addFreshEntity(lightning);
                    }
                }

                case 16 -> {
                    // Infecção Rápida
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 20, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 20, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 20, 0));
                }

                case 17 -> {
                    // Vampiro Sombrio
                    Vex vex = EntityType.VEX.create(level);
                    if (vex != null) {
                        ((Vex) vex).setCustomName(Component.literal("Beijo da Meia-Noite"));
                        vex.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        level.addFreshEntity(vex);
                    }
                }

                case 18 -> {
                    // Lançamento Espacial
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 3));
                }

                case 19 -> {
                    // Chuva de Lãs (simples)
                    Item[] wools = {
                            Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL,
                            Items.YELLOW_WOOL, Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL,
                            Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL, Items.PURPLE_WOOL, Items.BLUE_WOOL,
                            Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL
                    };

                    for (Item wool : wools) {
                        ItemEntity drop = new ItemEntity(level, player.getX(), player.getY() + 2, player.getZ(), new ItemStack(wool));
                        level.addFreshEntity(drop);
                    }
                }
                case 20 -> {
                    // Estrela Cadente
                    ItemStack netherStar = new ItemStack(Items.NETHER_STAR);
                    netherStar.setHoverName(Component.literal("Estrela Cadente").withStyle(ChatFormatting.AQUA));

                    // Dropa do céu com brilho
                    ItemEntity star = new ItemEntity(level,
                            player.getX() + random.nextDouble() * 2 - 1,
                            player.getY() + 10,
                            player.getZ() + random.nextDouble() * 2 - 1,
                            netherStar);
                    star.setGlowingTag(true);
                    level.addFreshEntity(star);

                    // Partículas e som especial
                    ((ServerLevel) level).sendParticles(ParticleTypes.END_ROD,
                            star.getX(), star.getY(), star.getZ(),
                            40, 0.5, 0.5, 0.5, 0.01);

                    level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_HIT, SoundSource.BLOCKS, 1.5f, 1.0f);
                }


                case 21 -> {
                    // Cura Absoluta
                    player.setHealth(player.getMaxHealth());
                    player.getFoodData().setFoodLevel(20);
                    player.getFoodData().setSaturation(20.0f);
                    level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 1.2f);
                }


                case 22 -> {
                    // Golem Guardião – Protetor Rúnico
                    IronGolem golem = EntityType.IRON_GOLEM.create(level);
                    if (golem != null) {
                        golem.setPos(player.getX() + 1, player.getY(), player.getZ() + 1);
                        golem.setPlayerCreated(true); // Faz o golem ser amigável ao player
                        golem.setCustomName(Component.literal("Bill").withStyle(ChatFormatting.DARK_AQUA));
                        level.addFreshEntity(golem);

                        // Partículas mágicas
                        ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                golem.getX(), golem.getY() + 2, golem.getZ(),
                                20, 0.5, 0.5, 0.5, 0.1);

                        level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.2f, 0.8f);
                    }
                }


                case 23 -> {
                    // XP Cósmico
                    player.giveExperiencePoints(500);
                    ((ServerLevel) level).sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 1, 0.5, 0.2);
                }


                case 24 -> {
                    // Totem do Caos
                    ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                    totem.setHoverName(Component.literal("Totem do Caos").withStyle(ChatFormatting.LIGHT_PURPLE));
                    ItemEntity drop = new ItemEntity(level, player.getX(), player.getY() + 1, player.getZ(), totem);
                    level.addFreshEntity(drop);
                    ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 1, 0.5, 0.1);
                }


                case 25 -> {
                    // Spawna 10 Chicken Jockeys
                    for (int i = 0; i < 10; i++) {
                        Chicken chicken = EntityType.CHICKEN.create(level);
                        Zombie zombie = EntityType.ZOMBIE.create(level);

                        if (chicken != null && zombie != null) {
                            double x = pos.getX() + random.nextDouble() * 4 - 2;
                            double z = pos.getZ() + random.nextDouble() * 4 - 2;
                            double y = pos.getY() + 1;

                            chicken.setPos(x, y, z);
                            zombie.setPos(x, y, z);
                            zombie.startRiding(chicken);

                            level.addFreshEntity(chicken);
                            level.addFreshEntity(zombie);
                        }
                    }
                }


                case 26 -> {
                    // Tesouro Submerso – Spawna um baú com loot aquático
                    BlockPos chestPos = pos.above();
                    level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

                    if (level.getBlockEntity(chestPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                        chest.setCustomName(Component.literal("Tesouro Submerso"));
                        chest.setItem(0, new ItemStack(Items.HEART_OF_THE_SEA));
                        chest.setItem(1, new ItemStack(Items.NAUTILUS_SHELL, 3));
                        chest.setItem(2, new ItemStack(Items.PRISMARINE_CRYSTALS, 8));
                        chest.setItem(3, new ItemStack(Items.EXPERIENCE_BOTTLE, 5));
                    }
                }


                case 27 -> {
                    // Companheiro Lobo – Spawna um lobo domesticado
                    Wolf wolf = EntityType.WOLF.create(level);
                    if (wolf != null) {
                        wolf.setPos(player.getX(), player.getY(), player.getZ());
                        wolf.tame(player);
                        wolf.setOwnerUUID(player.getUUID());
                        wolf.setCustomName(Component.literal("Wolfie"));
                        level.addFreshEntity(wolf);
                    }
                }


                case 28 -> {
                    // Barra de Netherite
                    ItemStack netherite = new ItemStack(Items.NETHERITE_INGOT);
                    netherite.setHoverName(Component.literal("Ingot Primordial").withStyle(ChatFormatting.DARK_PURPLE));
                    ItemEntity drop = new ItemEntity(level, player.getX(), player.getY() + 1, player.getZ(), netherite);
                    level.addFreshEntity(drop);
                }


                case 29 -> {
                    // Escudo Encantado
                    ItemStack shield = new ItemStack(Items.SHIELD);
                    EnchantmentHelper.setEnchantments(Map.of(Enchantments.THORNS, 2), shield);
                    shield.setHoverName(Component.literal("Escudo do Guardião").withStyle(ChatFormatting.BLUE));
                    ItemEntity drop = new ItemEntity(level, player.getX(), player.getY() + 1, player.getZ(), shield);
                    level.addFreshEntity(drop);
                }

                case 30 -> {
                    // Gaiola de Obsidian
                    BlockPos base = player.blockPosition();
                    for (int x = -1; x <= 1; x++) {
                        for (int y = 0; y <= 2; y++) {
                            for (int z = -1; z <= 1; z++) {
                                if (x == 0 && y == 1 && z == 0) continue; // não sobrescreve o player
                                BlockPos p = base.offset(x, y, z);
                                level.setBlockAndUpdate(p, Blocks.OBSIDIAN.defaultBlockState());
                            }
                        }
                    }
                }

                case 31 -> {
                    // Explosão de Fumaça
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), 100, 1, 1, 1, 0.1);
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 5, 0));
                }

                case 32 -> {
                    // Zumbis do Submundo
                    for (int i = 0; i < 5; i++) {
                        Zombie zombie = EntityType.ZOMBIE.create(level);
                        if (zombie != null) {
                            zombie.setPos(pos.getX() + random.nextDouble() * 4 - 2, pos.getY() + 1, pos.getZ() + random.nextDouble() * 4 - 2);
                            zombie.setCustomName(Component.literal("Submundo"));
                            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                            level.addFreshEntity(zombie);
                        }
                    }
                }

                case 33 -> {
                    // Transformação Maldita
                    int slot = player.getInventory().selected;
                    ItemStack item = player.getInventory().getItem(slot);
                    if (!item.isEmpty() && !item.is(Items.POISONOUS_POTATO)) {
                        player.getInventory().setItem(slot, new ItemStack(Items.POISONOUS_POTATO));
                        level.playSound(null, pos, SoundEvents.WITCH_CELEBRATE, SoundSource.PLAYERS, 1f, 1f);
                    }
                }

                case 34 -> {
                    // Tempestade Sombria
                    level.setRainLevel(1.0f);
                    level.setThunderLevel(1.0f);
                }

                case 35 -> {
                    // Túnel da Perdição
                    for (int y = 0; y < 10; y++) {
                        BlockPos p = player.blockPosition().below(y);
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                case 36 -> {
                    // Armadura Desgastada
                    for (ItemStack armor : player.getArmorSlots()) {
                        if (!armor.isEmpty()) {
                            armor.hurtAndBreak(armor.getMaxDamage() * 70 / 100, player, (p) -> {});
                        }
                    }
                }

                case 37 -> {
                    // Drenagem de XP
                    int currentXP = player.experienceLevel;
                    if (currentXP > 5) {
                        player.giveExperienceLevels(-5);
                    } else {
                        player.giveExperienceLevels(-currentXP);
                    }
                }

                case 38 -> {
                    // Veneno Arcano
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 5, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 5, 0));

                }

                case 39 -> {
                    // 50 Morcegos "coca-cola"
                    for (int i = 0; i < 50; i++) {
                        Bat bat = EntityType.BAT.create(level);
                        if (bat != null) {
                            double x = player.getX() + random.nextDouble() * 8 - 4;
                            double y = player.getY() + random.nextDouble() * 3;
                            double z = player.getZ() + random.nextDouble() * 8 - 4;

                            bat.setPos(x, y, z);
                            bat.setCustomName(Component.literal("coca-cola"));
                            level.addFreshEntity(bat);
                        }
            }
                }


            }
        }
        return InteractionResult.SUCCESS;
    }
}
