package com.iot.managerservice.domain.repository;


import com.iot.managerservice.domain.model.Edge;
import java.util.List;
import java.util.Optional;


public interface EdgeRepository {
    void save(Edge edge);
    Optional<Edge> findById(String edgeId);
    List<Edge> findAll();
    void deleteById(String edgeId);
}