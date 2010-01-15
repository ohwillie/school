function [ DX DY ] = my_gradient( A )
DX = 1/8 * my_conv2(A, [-1, -2, -1; 0, 0, 0; 1, 2, 1]);
DY = 1/8 * my_conv2(A, [-1, 0, 1; -2, 0, 2; -1, 0, 1]); 
end

