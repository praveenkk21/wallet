package com.WalletProject.WalletProject;

import com.WalletProject.WalletProject.dto.WalletDto;
import com.WalletProject.WalletProject.model.Wallet;
import com.WalletProject.WalletProject.repository.WalletRepo;
import com.WalletProject.WalletProject.service.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WalletProjectApplicationTests {

		@Mock
		private WalletRepo walletRepo;

		@InjectMocks
		private WalletServiceImpl walletService;

		private final Double START_BALANCE = 100.0;

		@BeforeEach
		void setUp() {
			ReflectionTestUtils.setField(walletService, "startBalance", START_BALANCE);
		}

		@Test
		void testAddWallet() {
			WalletDto walletDto = new WalletDto();
			walletDto.setUserId(123);

			Wallet expectedWallet = Wallet.builder()
					.userId(walletDto.getUserId())
					.balance(START_BALANCE)
					.build();

			when(walletRepo.save(any(Wallet.class))).thenReturn(expectedWallet);

			Wallet actualWallet = walletService.addWallet(walletDto);

			assertNotNull(actualWallet);
			assertEquals(START_BALANCE, actualWallet.getBalance());
			assertEquals(walletDto.getUserId(), actualWallet.getUserId());
			verify(walletRepo, times(1)).save(any(Wallet.class));
		}

		@Test
		void testGetWalletByContactNo() {
			int userId = 456;
			Wallet wallet = Wallet.builder().userId(userId).balance(START_BALANCE).build();

			when(walletRepo.findByUserId(userId)).thenReturn(wallet);

			Wallet retrievedWallet = walletService.getWalletByContactNo(userId);

			assertNotNull(retrievedWallet);
			assertEquals(userId, retrievedWallet.getUserId());
			assertEquals(START_BALANCE, retrievedWallet.getBalance());
			verify(walletRepo, times(1)).findByUserId(userId);
		}
	}

