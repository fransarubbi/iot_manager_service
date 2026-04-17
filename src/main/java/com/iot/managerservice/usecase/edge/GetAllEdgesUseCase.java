package com.iot.managerservice.usecase.edge;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.repository.EdgeRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class GetAllEdgesUseCase {

    private final EdgeRepository repository;

    public GetAllEdgesUseCase(EdgeRepository repository) {
        this.repository = repository;
    }

    public List<Edge> execute() {
        return repository.findAll();
    }
}
