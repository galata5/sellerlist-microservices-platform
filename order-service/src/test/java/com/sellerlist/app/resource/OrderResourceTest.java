package com.sellerlist.app.resource;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.service.OrderService;
import com.sellerlist.platform.security.InternalRequestHeaders;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderResource orderResource;

	@Test
	void findByIdUsesAuthenticatedUserHeaderForOwnershipLookup() throws Exception {
		final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.orderResource)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
		when(orderService.findById(9, 42)).thenReturn(OrderDto.builder()
				.orderId(42)
				.orderDate(LocalDateTime.of(2026, 4, 16, 12, 30))
				.orderDesc("checkout")
				.orderFee(21.5)
				.userId(9)
				.build());

		mockMvc.perform(get("/api/orders/42")
				.header(InternalRequestHeaders.AUTHENTICATED_USER_ID, "9"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderId").value(42))
				.andExpect(jsonPath("$.userId").value(9));

		verify(orderService).findById(9, 42);
	}
}
