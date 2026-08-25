package com.sellerlist.app.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sellerlist.app.domain.OutboxEvent;
import com.sellerlist.app.domain.OutboxEventStatus;
import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.repository.OutboxEventRepository;
import com.sellerlist.platform.events.OrderEvents;

@ExtendWith(MockitoExtension.class)
class OrderOutboxServiceTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@InjectMocks
	private OrderOutboxService orderOutboxService;

	@Test
	void enqueueOrderCreatedPersistsPendingOutboxEvent() {
		final OrderDto orderDto = OrderDto.builder()
				.orderId(42)
				.orderFee(55.25)
				.orderDesc("checkout")
				.orderDate(LocalDateTime.now())
				.cartDto(CartDto.builder().cartId(7).userId(9).build())
				.build();
		orderOutboxService.enqueueOrderCreated(orderDto);

		final ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
		verify(outboxEventRepository).save(captor.capture());
		assertThat(captor.getValue().getAggregateType()).isEqualTo("ORDER");
		assertThat(captor.getValue().getAggregateId()).isEqualTo("42");
		assertThat(captor.getValue().getEventType()).isEqualTo(OrderEvents.CREATED_ROUTING_KEY);
		assertThat(captor.getValue().getStatus()).isEqualTo(OutboxEventStatus.PENDING);
		assertThat(captor.getValue().getPayload())
				.contains("\"orderId\":42")
				.contains("\"cartId\":7")
				.contains("\"orderDescription\":\"checkout\"");
		assertThat(captor.getValue().getEventId()).isNotBlank();
	}
}
