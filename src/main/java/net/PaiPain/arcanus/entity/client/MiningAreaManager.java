package net.PaiPain.arcanus.entity.client;

import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Gerenciador de blocos marcados para mineração pelos Arcane Slaves.
 * Esses blocos devem ser definidos com o Arcane Hammer e serão
 * usados diretamente pelos Arcane Slaves durante o modo de mineração.
 */
public class MiningAreaManager {

    // Conjunto global de blocos marcados
    public static final Set<BlockPos> markedBlocks = new HashSet<>();

    /**
     * Limpa todos os blocos marcados para mineração.
     */
    public static void clearArea() {
        markedBlocks.clear();
    }

    /**
     * Adiciona um único bloco à área de mineração.
     *
     * @param pos posição do bloco a ser adicionado
     */
    public static void addBlock(BlockPos pos) {
        markedBlocks.add(pos.immutable());
    }

    /**
     * Adiciona vários blocos à área de mineração.
     *
     * @param positions conjunto de blocos a serem adicionados
     */
    public static void addBlocks(Set<BlockPos> positions) {
        for (BlockPos pos : positions) {
            markedBlocks.add(pos.immutable());
        }
    }

    /**
     * Verifica se um bloco está marcado para mineração.
     *
     * @param pos posição a verificar
     * @return true se estiver marcado
     */
    public static boolean isBlockMarked(BlockPos pos) {
        return markedBlocks.contains(pos);
    }

    /**
     * Remove um bloco da área de mineração.
     *
     * @param pos posição do bloco a ser removido
     */
    public static void removeMarkedBlock(BlockPos pos) {
        markedBlocks.remove(pos);
    }

    /**
     * Retorna todos os blocos atualmente marcados.
     *
     * @return conjunto imutável de blocos
     */
    public static Set<BlockPos> getMarkedBlocks() {
        return Collections.unmodifiableSet(markedBlocks);
    }

    /**
     * Verifica se não há nenhum bloco marcado.
     *
     * @return true se vazio
     */
    public static boolean isEmpty() {
        return markedBlocks.isEmpty();
    }
}
