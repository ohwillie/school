function [ B ] = my_rgb2gray( A )
B = A(:, :, 1) * 0.3 + A(:, :, 2) * 0.6 + A(:, :, 3) * 0.1;
end