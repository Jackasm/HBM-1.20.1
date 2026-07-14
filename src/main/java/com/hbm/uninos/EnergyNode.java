package com.hbm.uninos;

import com.hbm.api.energy.EnergyNet;
import com.hbm.uninos.networkproviders.EnergyNetProvider;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;

/**
 * Узел электрической сети. Содержит информацию о позиции и соединённых сторонах.
 */
public class EnergyNode extends GenNode<EnergyNet> {

    public EnergyNode(BlockPos... positions) {
        super(EnergyNetProvider.INSTANCE, positions);
        // Не нужно присваивать positions, так как это поле приватное в GenNode
    }

    public EnergyNode setConnections(Library.PosDir... connections) {
        // Конвертируем PosDir в ConnectionPoint (BlockPos + Direction)
        com.hbm.uninos.GenNode.ConnectionPoint[] points = new com.hbm.uninos.GenNode.ConnectionPoint[connections.length];
        for (int i = 0; i < connections.length; i++) {
            Library.PosDir pd = connections[i];
            points[i] = new com.hbm.uninos.GenNode.ConnectionPoint(
                    pd.pos(),
                    pd.dir()
            );
        }
        super.setConnections(points);
        return this;
    }

    // Для удобства – сеттер, принимающий готовые ConnectionPoint
    public EnergyNode setConnections(com.hbm.uninos.GenNode.ConnectionPoint... connections) {
        super.setConnections(connections);
        return this;
    }
}