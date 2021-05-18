package io.dataease.service.dataset;

import io.dataease.base.domain.DatasetTableUnion;
import io.dataease.base.mapper.DatasetTableUnionMapper;
import io.dataease.base.mapper.ext.ExtDatasetTableUnionMapper;
import io.dataease.commons.utils.AuthUtils;
import io.dataease.dto.dataset.DataSetTableUnionDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author gin
 * @Date 2021/5/6 6:03 下午
 */
@Service
@Transactional
public class DataSetTableUnionService {
    @Resource
    private DatasetTableUnionMapper datasetTableUnionMapper;
    @Resource
    private ExtDatasetTableUnionMapper extDatasetTableUnionMapper;

    public DatasetTableUnion save(DatasetTableUnion datasetTableUnion) {
        if (StringUtils.isEmpty(datasetTableUnion.getId())) {
            datasetTableUnion.setId(UUID.randomUUID().toString());
            datasetTableUnion.setCreateBy(AuthUtils.getUser().getUsername());
            datasetTableUnion.setCreateTime(System.currentTimeMillis());
            datasetTableUnionMapper.insert(datasetTableUnion);
        } else {
            datasetTableUnionMapper.updateByPrimaryKeySelective(datasetTableUnion);
        }
        return datasetTableUnion;
    }

    public void delete(String id) {
        datasetTableUnionMapper.deleteByPrimaryKey(id);
    }

    public List<DataSetTableUnionDTO> listByTableId(String tableId) {
        List<DataSetTableUnionDTO> sourceList = extDatasetTableUnionMapper.selectBySourceTableId(tableId);
        List<DataSetTableUnionDTO> targetList = extDatasetTableUnionMapper.selectByTargetTableId(tableId);
        sourceList.addAll(targetList.stream().map(ele -> {
            DataSetTableUnionDTO dto = new DataSetTableUnionDTO();
            dto.setId(ele.getId());

            dto.setSourceTableId(ele.getTargetTableId());
            dto.setSourceTableFieldId(ele.getTargetTableFieldId());
            dto.setSourceTableName(ele.getTargetTableName());
            dto.setSourceTableFieldName(ele.getTargetTableFieldName());

            dto.setTargetTableId(ele.getSourceTableId());
            dto.setTargetTableFieldId(ele.getSourceTableFieldId());
            dto.setTargetTableName(ele.getSourceTableName());
            dto.setTargetTableFieldName(ele.getSourceTableFieldName());

            dto.setSourceUnionRelation(ele.getTargetUnionRelation());
            dto.setTargetUnionRelation(ele.getSourceUnionRelation());

            dto.setCreateBy(ele.getCreateBy());
            dto.setCreateTime(ele.getCreateTime());
            return dto;
        }).collect(Collectors.toList()));

        sourceList.sort(Comparator.comparing(DatasetTableUnion::getCreateTime));
        return sourceList;
    }
}