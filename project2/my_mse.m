function mse = my_mse(mat1, mat2)
  mse = mean(mean((mat1 - mat2) .^ 2));
end