package com.sellerlist.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sellerlist.app.domain.id.OrderItemId;
import com.sellerlist.app.dto.OrderItemDto;
import com.sellerlist.app.exception.wrapper.OrderItemNotFoundException;
import com.sellerlist.app.helper.OrderItemMappingHelper;
import com.sellerlist.app.repository.OrderItemRepository;
import com.sellerlist.app.service.OrderItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
	
	private final OrderItemRepository orderItemRepository;
	
	@Override
	public List<OrderItemDto> findAll() {
		log.info("*** OrderItemDto List, service; fetch all orderItems *");
		return this.orderItemRepository.findAll()
				.stream()
					.map(OrderItemMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public OrderItemDto findById(final OrderItemId orderItemId) {
		log.info("*** OrderItemDto, service; fetch orderItem by id *");
		return this.orderItemRepository.findById(orderItemId)
				.map(OrderItemMappingHelper::map)
				.orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem with id: %s not found", orderItemId)));
	}
	
	@Override
	public OrderItemDto save(final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, service; save orderItem *");
		return OrderItemMappingHelper.map(this.orderItemRepository
				.save(OrderItemMappingHelper.map(orderItemDto)));
	}
	
	@Override
	public OrderItemDto update(final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, service; update orderItem *");
		return OrderItemMappingHelper.map(this.orderItemRepository
				.save(OrderItemMappingHelper.map(orderItemDto)));
	}
	
	@Override
	public void deleteById(final OrderItemId orderItemId) {
		log.info("*** Void, service; delete orderItem by id *");
		this.orderItemRepository.deleteById(orderItemId);
	}
	
	
	
}





